package sobad.code.status;

public enum FriendStatus {
    ACCEPTED ("Заявка в друзья принята!"),
    DECLINED("Заявка в друзья отклонена!"),
    WAITING("Ваша заявка в друзья для успешно отправлена"),
    NO_FRIENDS("Вы успешно удалили пользователя из друзей!"),
    NO_FOLLOWS("Вы успешно отписались от пользователя!"),
    FRIENDS("Вы друзья с этим пользователем!");

    private String statusMessage;

    FriendStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
