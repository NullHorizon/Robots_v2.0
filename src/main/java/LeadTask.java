import java.util.ArrayList;

/**
 * Created by shepkan on 05.01.2017.
 */
public class LeadTask extends Task {
    public LeadTask(){type="leader";}
    private LeadTask( Agent own, Agent rec, String mes){
        super(own,rec, mes);
        type="leader";
    }
    @Override
    public Task makeTask(Agent own, Agent rec, String mes) {
        return new LeadTask(own,rec,mes);
    }


    @Override
    public void onGetMessage(Message msg) {
        Agent t=msg.getTarget(), ft =msg.getFinalTarget();
        if (t!=ft){
            msg.getChain().add(t);
            if (t.isLead()){
                if (t.getClusterId()==ft.getClusterId()){
                    t.sendMessage(ft, new Message(msg.getContent(), msg.getChain(), ft,t,ft));
                } else {
                    Agent l=Clusterator.getCluster(ft.getClusterId()).getLeader();
                    t.sendMessage(l, new Message(msg.getContent(),msg.getChain(),l,t,ft));
                }
            } else {
                Agent l = Clusterator.getCluster(t.getClusterId()).getLeader();
                t.sendMessage(l, new Message(msg.getContent(), msg.getChain(),l,t,ft));
            }
        }
    }

    @Override
    public void step() {
        if (agOwner.isLead()) {
            if (agOwner.getClusterId() == recipient.getClusterId()) {
                agOwner.sendMessage(recipient, new Message(message, null, recipient, agOwner, recipient));
            } else {
                Agent l = Clusterator.getCluster(recipient.getClusterId()).getLeader();
                agOwner.sendMessage(l, new Message(message, null, l, agOwner, recipient));
            }
        } else {
            Agent l = Clusterator.getCluster(agOwner.getClusterId()).getLeader();
            agOwner.sendMessage(l, new Message(message, null, l, agOwner, recipient));
        }
        status=Status.SOLVED;
        agOwner.removeTask(this);
    }
}
