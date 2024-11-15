//package com.kienluu.jobfinderbackend.websocket.controller;
//
//import com.kienluu.jobfinderbackend.websocket.entity.User;
//import com.kienluu.jobfinderbackend.websocket.model.*;
//import com.kienluu.jobfinderbackend.websocket.service.GoogleCodeExchange;
//import com.kienluu.jobfinderbackend.websocket.service.MailService;
//import com.kienluu.jobfinderbackend.websocket.service.UserService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user")
//@AllArgsConstructor
//@Slf4j
//public class UserController {
//    private final UserService userService;
//    private final MailService mailService;
//    private final GoogleCodeExchange googleCodeExchange;
//
//    @PostMapping("/create")
//    public ResponseEntity<Object> createUser(@RequestBody UserDTO user) {
//        try {
//            User existUser = userService.findByEmail(user.getEmail());
//            if (existUser == null) {
//                MailTemplate mailTemplate = MailTemplate.builder()
//                        .to(user.getEmail())
//                        .useCase("Tao tai khoan")
//                        .build();
//                String code = mailService.send(mailTemplate);
//                Code code1 = Code.builder().code(code).user(user).build();
//                return ResponseEntity.ok(code1);
//            } else throw new RuntimeException("User already exist");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("User already exist");
//        }
//    }
//
//    @PostMapping("/code/success")
//    public ResponseEntity<Object> authenSuccess(@RequestBody UserDTO user) {
//        try {
//            UserDTO savedUser = userService.save(user);
//            return ResponseEntity.ok(savedUser);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
//        try {
//            User user = userService.findByEmailAndPass(request.getEmail(), request.getPassword());
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("The username or password is incorrect");
//        }
//    }
//
//
//    @PostMapping("/exchange_token")
//    public ResponseEntity<Object> exchangeGoogleToken(@RequestBody CodeExchange codeExchange) {
//        try {
//            GoogleUserInfo userInfoResponse = googleCodeExchange.exchange(codeExchange.getCode());
//            UserDTO user = UserDTO.builder()
//                    .avatar(userInfoResponse.getPicture())
//                    .email(userInfoResponse.getEmail())
//                    .userName(userInfoResponse.getName())
//                    .id("google_"+userInfoResponse.getId())
//                    .build();
//            UserDTO savedUser= userService.save(user);
//            if(savedUser == null) throw new RuntimeException("User already exist");
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }
//    }
//
//    @PostMapping("/google/login")
//    public ResponseEntity<Object> loginWithGoogle(@RequestBody CodeExchange codeExchange) {
//        try {
//            GoogleUserInfo userInfoResponse = googleCodeExchange.exchange(codeExchange.getCode());
//            User user = userService.findByEmail(userInfoResponse.getEmail());
//            if(user==null) throw new RuntimeException("User doesn't exist");
//            return ResponseEntity.ok(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<Object> findUser(@RequestParam("email") String email) {
//        try{
//            List<Participant> participants = userService.findParticipantByEmail(email);
//            return ResponseEntity.ok(participants);
//        }catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }
//    }
//
//    @GetMapping("/profile/{userId}")
//    public ResponseEntity<Object> findUserProfile(@PathVariable("userId") String userId) {
//        try{
//            User user = userService.findById(userId);
//            return ResponseEntity.ok(user);
//        }catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }
//    }
//
//    @PostMapping("/update")
//    public ResponseEntity<Object> updateUser(@RequestBody UserDTO user) {
//        try{
//            UserDTO userDTO =userService.updateProfile(user);
//            return ResponseEntity.ok(userDTO);
//        }catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }
//    }
//
//}
