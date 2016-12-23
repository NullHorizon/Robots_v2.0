/**
 * Created by shepkan on 21.12.2016.
 */
public abstract class Task {
    Agent owner, recipient;
    Message message;
    Status status;
    String type;
    public Task(){}
    public Task(Agent own, Agent rec, Message mes){
        owner=own;
        recipient=rec;
        message=mes;
        status=Status.UNSOLVED;
    }
    public abstract void step();
    public abstract void getAnswer(Agent from, Agent to);
    public abstract void getConnection(Agent from, Agent to);
    public enum Status{ UNSOLVED, SOLVED}
}
