package tasktracker.taskdata;

public class Task implements Cloneable{

    private long id;
    private TaskStatus status;
    private String name;
    private String description;

    public Task(String name, String description) {
        /** Михаил, добрый день! В лекции по код стайлу https://practicum.yandex.ru/learn/java-developer/courses/4793e202-2a57-441a-a0ab-f41851bd1ed5/sprints/23567/topics/7f595501-3878-4dff-9532-d27f361a8376/lessons/ec800062-4877-4b15-b378-ebeba985d378/
         * в проектах требуется использовать стайл "Практикума". В ней же в пункте "3. Фигурные скобки." говориться, что
         * будет использоваться K&R. "При расстановке фигурных скобок мы будем использовать стиль “K&R” (англ. Kernighan
         * and Ritchie), который назван в честь Брайана Кёрнигана и Денниса Ритчи — авторов книги «Язык программирования
         * Си»." https://en.wikipedia.org/wiki/Indentation_style#Variant:_Linux_kernel Вот здесь, в описании K&R
         * черным по белому сказано, что для одноблочного кода в иф, елс и тп СКОБКИ НЕ ОБЯЗАТЕЛЬНЫ.
         * Поэтому, перелопачивать весь проект я не буду, тем более в данном случае считаю себя правым.
         * У меня уже меняется второй ревьюер, и второй раз он выдает не серьезные замечания, которые требуют потратить
         * кучу времени, при чем предидущий ревьюер таких замечаний не делал, да при этом это в общем-то и не ошибка.
         * Я приветствую, когда ревьюер дает много замечаний, но такие замечания немного раздражают. :)
         */
        if (name == null || name.isBlank())
            throw new TaskInvalidException("Cannot create unnamed task");
        if (description == null)
            description = "";
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(TaskStatus status, String name, String description){
        this(name, description);
        if (status == null)
            throw new TaskInvalidException("Cannot create task with null status");
        this.status = status;
    }

    public Task(long id, TaskStatus status, String name, String description) {
        this(status, name, description);
        if (id < 0)
            throw new TaskInvalidException("Cannot create task with ID less than 0");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank())
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        if (id >= 0)
            this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status != null)
            this.status = status;
    }

    @Override
    public Task clone() {
        return new Task(this.id, this.status, this.name, this .description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length()='" + (description != null ? description.length() : null) + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (status != task.status) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        return description != null ? description.equals(task.description) : task.description == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
