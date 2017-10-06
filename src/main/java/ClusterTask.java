import java.util.ArrayList;

public class ClusterTask extends Task {
    private ArrayList<Agent> list, noFriendList;
    private Agent search_agent;

    ClusterTask(){type="cluster";}

    private ClusterTask(Agent own, Agent rec, String mes){
        super(own, rec,mes);
        list= new ArrayList<>();
        list.addAll(agOwner.getConnected());
        noFriendList= new ArrayList<>();
        for (int i = 0; i< Simulator.getAgents().size(); i++){
            if (list.indexOf(Simulator.getAgents().get(i))==-1){
                noFriendList.add(Simulator.getAgents().get(i));
            }
        }
        search_agent=null;
        type="cluster";
    }

    public Task makeTask(Agent own, Agent rec, String mes) {
        return new ClusterTask( own, rec, mes);
    }

    private void getAnswer(Agent from, Agent to) {
        to.addConnected(from);
        from.addConnected(to);
        to.nextTaskStep();
    }

    private void getConnection(Agent from, Agent to) {
        to.sendMessage(from, new Message("I am "+to.getId(),null, from, to, Message.MSGType.ANSWER));
    }

    public void onGetMessage(Message msg) {
        if (msg.isNegative() && msg.getTarget()!=msg.getFinalTarget()){
            Simulator.stats.addNegativeResended();
            if (!msg.getTarget().isSaboteur()){
                msg.setNegative(false);
            }
        }
        if (msg.getType()== Message.MSGType.FEED_BACK && msg.getContent().contains("_TARG")){
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
            case SEARCH:
                boolean finded=false;
                int n = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_SEARCH")));
                for (int i=0; i<msg.getTarget().getConnected().size();i++){
                    if (msg.getTarget().getConnected().get(i).getClusterId()==n){
                        Message m= new Message("FINDED!", null, msg.getFrom(), msg.getTarget(), Message.MSGType.FINDED);
                        msg.getTarget().sendMessage(msg.getFrom(), m);
                        finded=true;
                        break;
                    }
                }
                if( !finded) {
                    Message m = new Message("NOT FINDED", null, msg.getFrom(), msg.getTarget(), Message.MSGType.SEARCH_FALL);
                    msg.getTarget().sendMessage(msg.getFrom(), m);
                }
                break;
            case SEARCH_FALL:
                msg.getTarget().nextTaskStep();
                break;
            case FINDED:
                msg.getTarget().nextTaskStep();
                break;
            case MESSAGE:
                if (msg.getContent().contains("_TARG")) {
                    Simulator.stats.addDistance(msg.getFrom().getPos().distance(msg.getTarget().getPos()));
                    Agent a = msg.getTarget();
                    if (a != msg.getFinalTarget()) {
                        if (a.getClusterId() == msg.getFinalTarget().getClusterId()) {
                            Message m = new Message(msg.getContent(), msg.getChain(), msg.getFinalTarget(), a);
                            if (a.isSaboteur()){
                                m.setNegative(true);
                                if (!msg.isNegative()){
                                    Simulator.stats.addNegativeSource();
                                    if (m.getFinalTarget()!=m.getTarget()){
                                        Simulator.stats.addNegativeWithInter();
                                    }
                                }
                            }
                            a.sendMessage(msg.getFinalTarget(), m);
                        } else {
                            for (int i = 0; i < a.getConnected().size(); i++) {
                                if (a.getConnected().get(i).getClusterId() == msg.getFinalTarget().getClusterId()) {
                                    Message m = new Message(msg.getContent(), msg.getChain(), a.getConnected().get(i),
                                            a, msg.getFinalTarget());
                                    if (a.isSaboteur()){
                                        m.setNegative(true);
                                    }
                                    if (m.getFinalTarget()!=m.getTarget()){
                                        Simulator.stats.addNegativeWithInter();
                                    }
                                    a.sendMessage(a.getConnected().get(i), m);
                                }
                            }
                        }
                    } else {
                        Simulator.stats.addChain(msg.getChain().size());
                        int k = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_TARG")));
                        if (msg.isNegative()){
                            msg.getTarget().setBroken(true);
                        }
                        Agent feedBackReciver=msg.getChain().get(msg.getChain().size()-1);
                        Message feedBack=new Message(k+"_TARG_FEED_BACK",msg.getChain(),feedBackReciver, msg.getTarget(),
                                msg.getChain().get(0), Message.MSGType.FEED_BACK);
                        if (msg.getTarget().isSaboteur()){
                            feedBack.setNegative(true);
                            Simulator.stats.addNegativeSource();
                            if (feedBack.getFinalTarget()!=feedBack.getTarget()){
                                Simulator.stats.addNegativeWithInter();
                            }
                        }
                        Simulator.taskType.feedBack(feedBack);
                        //Simulator.tasks.get(k).solve();
                    }
                }
                break;
        }
    }

    public void step(){
        steps++;
        boolean sended=false;
        if (list.indexOf(recipient)!=-1){
            Message m=new Message(message, null, recipient, agOwner);
            if (agOwner.isSaboteur()){
                m.setNegative(true);
                Simulator.stats.addNegativeSource();
                if (m.getFinalTarget()!=m.getTarget()){
                    Simulator.stats.addNegativeWithInter();
                }
            }
            agOwner.sendMessage(recipient, m);
            sended=true;
            agOwner.removeTask(this);
        }
        if (!sended && search_agent!=null){
            for (int i=0; i<search_agent.getConnected().size();i++){
                if (search_agent.getConnected().get(i).getClusterId()==recipient.getClusterId()){
                    Message m= new Message(message, null,search_agent.getConnected().get(i),agOwner, recipient );
                    if (agOwner.isSaboteur()){
                        m.setNegative(true);
                        Simulator.stats.addNegativeSource();
                        if (m.getFinalTarget()!=m.getTarget()){
                            Simulator.stats.addNegativeWithInter();
                        }
                    }
                    agOwner.sendMessage(search_agent.getConnected().get(i),m);
                    sended=true;
                    agOwner.removeTask(this);
                    break;
                }
            }
            search_agent=null;
        }
        if (!sended && list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getClusterId() == recipient.getClusterId()) {
                    Message m=new Message(message, null, list.get(i), agOwner, recipient);
                    if (agOwner.isSaboteur()){
                        m.setNegative(true);
                        Simulator.stats.addNegativeSource();
                        if (m.getFinalTarget()!=m.getTarget()){
                            Simulator.stats.addNegativeWithInter();
                        }
                    }
                    agOwner.sendMessage(list.get(i), m);
                    sended=true;
                    agOwner.removeTask(this);
                    break;
                }
            }
        }
        if (!sended && list.size()>0){
            Agent a=list.remove(StRandom.nextInt(list.size()));
            Message m=new Message(recipient.getClusterId()+"_SEARCH", null, a, agOwner, Message.MSGType.SEARCH);
            agOwner.sendMessage(a, m);
            search_agent=a;
            sended=true;
        }
        if (!sended){
            Agent a=noFriendList.remove(StRandom.nextInt(noFriendList.size()));
            Message m=new Message("Hello. Who are you?", null, a, agOwner, Message.MSGType.CONNECTION);
            agOwner.sendMessage(a, m);
            list.add(a);
        }
    }
}
