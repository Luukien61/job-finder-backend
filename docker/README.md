```shell
sudo growpart /dev/nvme0n1 1
sudo resize2fs /dev/nvme0n1p1

```
---

## 1. Setup Backend trên EC2

### Chuẩn bị EC2
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Cài đặt dependencies cơ bản
sudo apt install -y nginx certbot python3-certbot-nginx

```


### Cấu hình Nginx Reverse Proxy
```nginx
# /etc/nginx/sites-available/backend
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:8088;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # Timeout settings
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```
### To remove a nginx config file
```shell
sudo rm /etc/nginx/sites-enabled/backend
sudo rm /etc/nginx/sites-available/backend
```
### Security Group EC2
- **Inbound Rules:**
    - Port 22 (SSH) - từ IP của bạn
    - Port 80 (HTTP) - từ 0.0.0.0/0
    - Port 443 (HTTPS) - từ 0.0.0.0/0
    - **KHÔNG** mở port 8088 ra public

## 2. Setup Frontend trên S3

### Tạo S3 Bucket
```bash
# Tạo bucket
aws s3 mb s3://your-frontend-bucket

# Build frontend
npm run build

# Upload lên S3
aws s3 sync ./dist s3://your-frontend-bucket --delete
```

### Cấu hình S3 Bucket
```json
# Bucket Policy - cho phép CloudFlare đọc
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::your-frontend-bucket/*"
    }
  ]
}
```

### Enable Static Website Hosting
- **Index document:** index.html
- **Error document:** index.html (cho SPA routing)

## 3. Setup CloudFlare

### DNS Records
```
# Frontend
Type: CNAME
Name: www (hoặc @)
Content: your-frontend-bucket.s3-website-region.amazonaws.com
Proxy status: Proxied (cloud màu cam)

# Backend API
Type: A
Name: api
Content: <EC2 Public IP>
Proxy status: Proxied
```

### SSL/TLS Settings
- **SSL/TLS encryption mode:** Full hoặc Full (strict)
- CloudFlare sẽ tự cấp SSL certificate miễn phí

### Page Rules (Optional nhưng nên có)
```
Rule 1: api.yourdomain.com/*
- SSL: Full
- Cache Level: Bypass

Rule 2: yourdomain.com/*
- Browser Cache TTL: 4 hours
- Cache Level: Standard
```

### Security Settings
- **Enable:**
    - Bot Fight Mode
    - Security Level: Medium
    - Challenge Passage: 30 minutes

- **Firewall Rules:** Tùy chỉnh theo nhu cầu

## 4. Setup SSL cho Backend (Nginx + Let's Encrypt)

```bash
# Cài SSL certificate
sudo certbot --nginx -d api.yourdomain.com

# Auto renew
sudo certbot renew --dry-run
```

## 5. Environment Variables

### Backend (.env)
```bash
NODE_ENV=production
PORT=8088
DATABASE_URL=your_db_url
CORS_ORIGIN=https://yourdomain.com
JWT_SECRET=your_secret_key
```

### Frontend (.env.production)
```bash
VITE_API_URL=https://api.yourdomain.com
# hoặc
REACT_APP_API_URL=https://api.yourdomain.com
```

## 6. Monitoring & Logging

### Backend Logs
```bash
# Xem logs PM2
pm2 logs backend

# Setup log rotation
pm2 install pm2-logrotate
```

### CloudWatch (AWS)
- Enable CloudWatch cho EC2
- Set up alerts cho CPU, Memory, Disk



