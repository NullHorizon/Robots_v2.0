/**
 * Created by shepkan on 21.12.2016.
 */
public abstract class Task {
    Agent agOwner, recipient;
    String message;
    Status status;
    String type;
    public Task(){}
    protected Task(Agent own, Agent rec, String mes){
        agOwner=own;
        recipient=rec;
        message=mes;
        status=Status.UNSOLVED;
    }
    public String getType(){return type;}
    public abstract Task makeTask(Agent own, Agent rec, String mes);
    public abstract void onGetMessage(Message msg);
    public abstract void step();
    public enum Status{ UNSOLVED, SOLVED}

}
