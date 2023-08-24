package sobad.code.status;

public enum Status {
    ACCEPTED("Заявка в друзья принята!"),
    DECLINED("Заявка в друзья отклонена!"),
    WAITING("Ваша заявка в друзья для успешно отправлена"),
    NO_FRIENDS("Вы успешно удалили пользователя из друзей!");

    private String statusMessage;

    Status(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
