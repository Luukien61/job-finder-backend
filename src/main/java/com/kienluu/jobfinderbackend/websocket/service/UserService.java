//package com.kienluu.jobfinderbackend.websocket.service;
//
//import com.kienluu.jobfinderbackend.websocket.entity.User;
//import com.kienluu.jobfinderbackend.mapper.CustomMapper;
//import com.kienluu.jobfinderbackend.websocket.model.Participant;
//import com.kienluu.jobfinderbackend.websocket.model.UserDTO;
//import com.kienluu.jobfinderbackend.websocket.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//    private final CustomMapper mapper;
//
//    public boolean isGoogleAccount(String email) {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            return false;
//        } else {
//            String id = user.getId();
//            return id.startsWith("google");
//        }
//    }
//
//    public UserDTO save(UserDTO user) throws RuntimeException {
//        User existUser = userRepository.findByEmail(user.getEmail());
//        if (existUser == null) {
//            User user1=mapper.toUser(user);
//            user1.setConversations(Collections.emptySet());
//            userRepository.save(user1);
//            return user;
//        } else throw new RuntimeException("User already exists");
//    }
//
//    public User findByEmailAndPass(String email, String pass) {
//        boolean isGoogle = isGoogleAccount(email);
//        if (isGoogle) throw new RuntimeException("This account is google");
//
//        User user = userRepository.findByEmailAndPassword(email, pass);
//        if (user != null) {
//            return user;
//        } else throw new RuntimeException("User not found");
//    }
//
//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    public User findById(String id) {
//        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    public List<Participant> findParticipantByEmail(String email) {
//        List<User> users = userRepository.findUserByEmailContaining(email);
//        if (!users.isEmpty()) {
//            return users.stream().map(this::convertToParticipant).toList();
//        } else return Collections.emptyList();
//
//    }
//
//    public UserDTO updateProfile(UserDTO user) {
//        User existUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
//        existUser.setUserName(user.getUserName());
//        existUser.setAvatar(user.getAvatar());
//        existUser.setPassword(user.getPassword());
//        existUser.setPhone(user.getPhone());
//        existUser = userRepository.save(existUser);
//        return mapper.toUserDTO(existUser);
//
//    }
//
//    private Participant convertToParticipant(User user) {
//        return Participant.builder()
//                .name(user.getUserName())
//                .id(user.getId())
//                .avatar(user.getAvatar())
//                .build();
//    }
//
//
//}
