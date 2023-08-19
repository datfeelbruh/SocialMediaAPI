package sobad.code.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sobad.code.status.FriendStatus;

@Entity
@Table(name = "friends")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    @Id
    private Long userId;
    private Long friendId;
    private FriendStatus status;
}
