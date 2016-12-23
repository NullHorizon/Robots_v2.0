import java.util.ArrayList;

/**
 * Created by shepkan on 22.12.2016.
 */
public class SimpleTask extends Task {
    ArrayList<Agent> list;

    public SimpleTask(){type="simple";}
    public SimpleTask(Agent own, Agent rec, Message mes) {
        super(own, rec, mes);
        list=(ArrayList<Agent>) main.agents.clone();
        type="simple";
    }

    public void step() {
        if (owner.getConnected().indexOf(recipient)==-1){
            Agent b=list.remove(StRandom.nextInt(list.size()));
            while (owner.getConnected().indexOf(b)!=-1){
                b=list.remove(StRandom.nextInt(list.size()));
            }
            owner.sendMessage(b, new Message("Hello. Who are you?", null,b, Message.MessageType.CONNECTION));
        } else {
            owner.sendMessage(recipient, message);
            status=Status.SOLVED;
            owner.removeTask(this);
        }
    }

    public void getAnswer(Agent from, Agent to) {
        to.addConnected(from);
        to.nextTaskStep();
    }

    public void getConnection(Agent from, Agent to) {
        to.addConnected(from);
        to.sendMessage(from, new Message("I am "+to.getId(),null, from, Message.MessageType.ANSWER));
    }
}
