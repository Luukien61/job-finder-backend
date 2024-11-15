//package com.kienluu.jobfinderbackend.websocket.repository;
//
//import com.kienluu.jobfinderbackend.websocket.entity.Conversation;
//import com.kienluu.jobfinderbackend.websocket.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//
//@Repository
//public interface ConversationRepository extends JpaRepository<Conversation, String> {
//
//    //List<Conversation> findConversationByUser1IdOrUser2Id(String user1Id, String user2Id);
//
//    Set<Conversation> findConversationByUsersIn(Collection<Set<User>> users);
//
//    Conversation findConversationById(String id);
//
//    //Conversation findConversationByUser1IdAndUser2Id(String user1Id, String user2Id);
//
//
//    @Query("SELECT c FROM Conversation c JOIN c.users u WHERE u.id = :userId")
//    List<Conversation> findByUserId(@Param("userId") String userId);
//
//
//    @Query("SELECT c FROM Conversation c JOIN c.users u WHERE u.id IN :userIds GROUP BY c HAVING COUNT(DISTINCT u.id) = :userCount")
//    List<Conversation> findByUserIds(@Param("userIds") List<String> userIds, @Param("userCount") Long userCount);
//
//    @Query("SELECT c FROM Conversation c " +
//            "JOIN c.users u " +
//            "WHERE u.id IN :userIds " +
//            "GROUP BY c " +
//            "HAVING COUNT(DISTINCT u.id) = 2 " +
//            "AND COUNT(CASE WHEN u.id NOT IN :userIds THEN 1 END) = 0")
//    List<Conversation> findConversationByExactTwoUsers(@Param("userIds") List<String> userIds);
//
//
//}
