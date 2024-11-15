package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements IUserService{

    private UserRepository userRepository;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

    public String registerUser(UserEntity user, UserRole role) {
        // Kiểm tra xem email có tồn tại không
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists!";
        }
        user.setUserId("u_"+generateCustomUserId());
        // Mã hóa mật khẩu người dùng
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(user.getPassword());


        if (role == UserRole.EMPLOYER || role == UserRole.USER) {
            user.setRole(role);
        } else {
            return "Invalid role!";
        }

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);
        return "Registration successful!";
    }

    private Long generateCustomUserId() {
        // Logic để tạo giá trị userId của bạn (có thể dựa trên thời gian, UUID, v.v.)
        return System.currentTimeMillis();  // Ví dụ sử dụng thời gian hiện tại
    }


}
