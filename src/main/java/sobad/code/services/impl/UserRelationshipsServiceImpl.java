//package sobad.code.services.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import sobad.code.entities.Follower;
//import sobad.code.entities.Friend;
//import sobad.code.repositories.FollowerRepository;
//import sobad.code.repositories.FriendRepository;
//import sobad.code.repositories.UserRepository;
//import sobad.code.status.FriendStatus;
//import sobad.code.entities.User;
//import sobad.code.services.UserRelationshipsService;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UserRelationshipsServiceImpl implements UserRelationshipsService {
//    private final FriendRepository friendRepository;
//    private final FollowerRepository followerRepository;
//
//    @Override
//    public void addFriendRequest(User currentUser, User user) {
//        Follower follower = Follower.builder()
//                .subscriberId(currentUser.getId())
//                .userId(user.getId())
//                .build();
//
//        Friend friend = Friend.builder()
//                .userId(currentUser.getId())
//                .friendId(user.getId())
//                .status(FriendStatus.WAITING)
//                .build();
//
//        friendRepository.save(friend);
//        followerRepository.save(follower);
//    }
//
//    @Override
//    public String decideFriendRequest(User currentUser, User user, FriendStatus friendStatus) {
//        Friend friend = friendRepository.findByUserId(user.getId()).orElseThrow();
//        if (friendStatus.equals(FriendStatus.ACCEPTED)) {
//            friend.setStatus(FriendStatus.FRIENDS);
//            Follower follower = Follower.builder()
//                    .subscriberId(currentUser.getId())
//                    .userId(user.getId())
//                    .build();
//            friendRepository.save(friend);
//            followerRepository.save(follower);
//
//            return friendStatus.getStatusMessage();
//        }
//
//        friendRepository.delete(friend);
//        return friendStatus.getStatusMessage();
//    }
//
//    @Override
//    public void removeFollower(User currentUser) {
//        followerRepository.deleteById(currentUser.getId());
//    }
//
//    @Override
//    public void removeFriend(User currentUser, User user) {
//        friendRepository.deleteById(user.getId());
//        followerRepository.deleteById(currentUser.getId());
//    }
//
//    public boolean isFriends(Long userOneId, Long userTwoId) {
//        Friend friendship = friendRepository.findFriendship(userOneId, userTwoId).orElseThrow();
//        return friendship.getStatus().equals(FriendStatus.FRIENDS);
//    }
//}
