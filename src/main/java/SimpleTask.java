import java.util.ArrayList;

/**
 * Created by shepkan on 22.12.2016.
 */
public class SimpleTask extends Task {
    ArrayList<Agent> list;

    public SimpleTask(){type="simple";}
    private SimpleTask(Agent own, Agent rec, String mes){
        super(own, rec,mes);
        list=(ArrayList<Agent>) main.agents.clone();
        type="simple";
    }
    public Task makeTask(Agent own, Agent rec, String mes) {
        return new SimpleTask( own, rec, mes);
    }

    public void step() {
        if (agOwner.getConnected().indexOf(recipient)==-1){
            Agent b=list.remove(StRandom.nextInt(list.size()));
            while (agOwner.getConnected().indexOf(b)!=-1){
                b=list.remove(StRandom.nextInt(list.size()));
            }
            agOwner.sendMessage(b, new Message("Hello. Who are you?", null,b, agOwner, Message.MSGType.CONNECTION));
        } else {
            Message compMsg= new Message(message, null,recipient,agOwner);
            agOwner.sendMessage(recipient, compMsg);
            status=Status.SOLVED;
            agOwner.removeTask(this);
        }
    }

    public void getAnswer(Agent from, Agent to) {
        to.addConnected(from);
        from.addConnected(to);
        to.nextTaskStep();
    }

    public void getConnection(Agent from, Agent to) {
        to.sendMessage(from, new Message("I am "+to.getId(),null, from, to, Message.MSGType.ANSWER));
    }

    @Override
    public void onGetMessage(Message msg) {
        switch (msg.getType()){
            case ANSWER:
                getAnswer(msg.getFrom(), msg.getTarget());
                break;
            case CONNECTION:
                getConnection(msg.getFrom(), msg.getTarget());
                break;
        }
    }
}
