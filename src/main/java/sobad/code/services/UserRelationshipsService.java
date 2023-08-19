package sobad.code.services;

import sobad.code.entities.Friend;
import sobad.code.entities.User;
import sobad.code.status.FriendStatus;

public interface UserRelationshipsService {
    void addFriendRequest(User to, User from);
    String decideFriendRequest(User from, User to, FriendStatus status);
    void removeFollower(User user);
    void removeFriend(User userOne, User userTwo);
}
