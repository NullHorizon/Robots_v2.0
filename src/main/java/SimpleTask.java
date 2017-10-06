import java.util.ArrayList;

public class SimpleTask extends Task {
    private ArrayList<Agent> list;


    public SimpleTask(){type="simple";}
    private SimpleTask(Agent own, Agent rec, String mes){
        super(own, rec,mes);
        steps=0;
        list=new ArrayList<Agent>();
        for (int i = 0; i< Simulator.getAgents().size(); i++){
            list.add(Simulator.getAgents().get(i));
        }
        type="simple";
    }
    public Task makeTask(Agent own, Agent rec, String mes) {
        return new SimpleTask( own, rec, mes);
    }

    public void step() {
        steps++;
        if (agOwner.getConnected().contains(recipient)){
            Message compMsg= new Message(message, null,recipient,agOwner);
            if (agOwner.isSaboteur()) {
                compMsg.setNegative(true);
                Simulator.stats.addNegativeSource();
                if (compMsg.getFinalTarget()!=compMsg.getTarget()){
                    Simulator.stats.addNegativeWithInter();
                }
            }
            agOwner.sendMessage(recipient, compMsg);
            agOwner.removeTask(this);

        } else {
            Agent b=list.remove(StRandom.nextInt(list.size()));
            while (agOwner.getConnected().indexOf(b)!=-1){
                b=list.remove(StRandom.nextInt(list.size()));
            }
            agOwner.sendMessage(b, new Message("Hello. Who are you?", null,b, agOwner, Message.MSGType.CONNECTION));
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
        if (msg.isNegative() && msg.getFinalTarget()!=msg.getTarget()){
            Simulator.stats.addNegativeResended();
        }
        if (msg.getType()== Message.MSGType.FEED_BACK && msg.getContent().indexOf("_TARG")!=-1){
            feedBack(msg);
            return;
        }
        switch (msg.getType()){
            case ANSWER:
                getAnswer(msg.getFrom(), msg.getTarget());
                break;
            case CONNECTION:
                getConnection(msg.getFrom(), msg.getTarget());
                break;
            case MESSAGE:
                if (msg.getContent().indexOf("_TARG")!=-1) {
                    int n = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_TARG")));
                    Simulator.stats.addChain(msg.getChain().size());
                    Simulator.stats.addDistance(msg.getFrom().getPos().distance(msg.getTarget().getPos()));
                    if (msg.isNegative()){
                        msg.getTarget().setBroken(true);
                    }
                    Agent feedBackReciver=msg.getChain().get(msg.getChain().size()-1);
                    Message feedBack=new Message(n+"_TARG_FEED_BACK",msg.getChain(),feedBackReciver, msg.getTarget(),
                            msg.getChain().get(0), Message.MSGType.FEED_BACK);
                    if (msg.getTarget().isSaboteur()){
                        feedBack.setNegative(true);
                        if (feedBack.getFinalTarget()!=feedBack.getTarget()){
                            Simulator.stats.addNegativeWithInter();
                        }
                        if (feedBack.getFinalTarget()!=feedBack.getTarget()){
                            Simulator.stats.addNegativeWithInter();
                        }
                        Simulator.stats.addNegativeSource();
                    }
                    Simulator.taskType.feedBack(feedBack);

                    //Simulator.tasks.get(n).solve();
                }
                break;
        }
    }
}
