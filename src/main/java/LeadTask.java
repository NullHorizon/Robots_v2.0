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
        if (msg.isNegative() && (msg.getTarget()!=msg.getFinalTarget())){
            Simulator.stats.addNegativeResended();
            if (!msg.getTarget().isSaboteur()){
                msg.setNegative(false);
            }
        }
        if (msg.getType()== Message.MSGType.FEED_BACK && msg.getContent().indexOf("_TARG")!=-1){
            feedBack(msg);
            return;
        }
        if (msg.getContent().indexOf("_TARG")!=-1){
            Simulator.stats.addDistance(msg.getFrom().getPos().distance(msg.getTarget().getPos()));
        }
        Agent t=msg.getTarget(), ft =msg.getFinalTarget();
        if (t!=ft){
            //msg.getChain().add(t);
            if (t.isLead()){
                if (t.getClusterId()==ft.getClusterId()){
                    Message m=new Message(msg.getContent(), msg.getChain(), ft,t,ft);
                    if (t.isSaboteur()){
                        m.setNegative(true);
                        if (!msg.isNegative()){
                            Simulator.stats.addNegativeSource();
                            if (m.getFinalTarget()!=m.getTarget()){
                                Simulator.stats.addNegativeWithInter();
                            }
                        }
                    }
                    t.sendMessage(ft, m);
                } else {
                    Agent l=Clusterator.getCluster(ft.getClusterId()).getLeader();
                    Message m=new Message(msg.getContent(),msg.getChain(),l,t,ft);
                    if (t.isSaboteur()){
                        m.setNegative(true);
                        if (!msg.isNegative()){
                            Simulator.stats.addNegativeSource();
                            if (m.getFinalTarget()!=m.getTarget()){
                                Simulator.stats.addNegativeWithInter();
                            }
                        }
                    }
                    t.sendMessage(l, m);
                }
            } else {
                Agent l = Clusterator.getCluster(t.getClusterId()).getLeader();
                Message m=new Message(msg.getContent(), msg.getChain(),l,t,ft);
                if (t.isSaboteur()){
                    m.setNegative(true);
                    if (!msg.isNegative()){
                        Simulator.stats.addNegativeSource();
                        if (m.getFinalTarget()!=m.getTarget()){
                            Simulator.stats.addNegativeWithInter();
                        }
                    }
                }
                t.sendMessage(l, m);
            }
        } else {
            if (msg.getContent().indexOf("_TARG")!=-1) {
                Simulator.stats.addChain(msg.getChain().size());
                int n = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_TARG")));
                if (msg.isNegative()){
                    msg.getFinalTarget().setBroken(true);
                }
                Agent feedBackReciver=msg.getChain().get(msg.getChain().size()-1);
                Message feedBack=new Message(n+"_TARG_FEED_BACK",msg.getChain(),feedBackReciver, msg.getTarget(),
                        msg.getChain().get(0), Message.MSGType.FEED_BACK);
                if (msg.getTarget().isSaboteur()){
                    feedBack.setNegative(true);
                    if (feedBack.getFinalTarget()!=feedBack.getTarget()){
                        Simulator.stats.addNegativeWithInter();
                    }
                    Simulator.stats.addNegativeSource();
                }
                Simulator.taskType.feedBack(feedBack);
                //Simulator.tasks.get(n).solve();
            }
        }
    }

    @Override
    public void step() {
        steps++;
        if (agOwner.isLead()) {
            if (agOwner.getClusterId() == recipient.getClusterId()) {
                Message m=new Message(message, null, recipient, agOwner, recipient);
                if (agOwner.isSaboteur()){
                    m.setNegative(true);
                    Simulator.stats.addNegativeSource();
                    if (m.getFinalTarget()!=m.getTarget()){
                        Simulator.stats.addNegativeWithInter();
                    }
                }
                agOwner.sendMessage(recipient, m);
            } else {
                Agent l = Clusterator.getCluster(recipient.getClusterId()).getLeader();
                Message m=new Message(message, null, l, agOwner, recipient);
                if (agOwner.isSaboteur()){
                    m.setNegative(true);
                    Simulator.stats.addNegativeSource();
                    if (m.getFinalTarget()!=m.getTarget()){
                        Simulator.stats.addNegativeWithInter();
                    }
                }
                agOwner.sendMessage(l, m);
            }
        } else {
            Agent l = Clusterator.getCluster(agOwner.getClusterId()).getLeader();
            Message m=new Message(message, null, l, agOwner, recipient);
            if (agOwner.isSaboteur()){
                m.setNegative(true);
                Simulator.stats.addNegativeSource();
                if (m.getFinalTarget()!=m.getTarget()){
                    Simulator.stats.addNegativeWithInter();
                }
            }
            agOwner.sendMessage(l, m);
        }
        agOwner.removeTask(this);
    }
}
