import java.util.ArrayList;
import java.util.List;

public class InfPhyTask extends Task {
    private int iterationForThisExp;
    private Progress progress;
    private int iterationNum;
    private boolean broken_message=false;
    private List<Agent> unique_center_inf=new ArrayList<>(),unique_center_phy=new ArrayList<>();
    public InfPhyTask(){
        type="InfPhy";
        iterationNum=0;
    }
    public InfPhyTask(Agent own, Agent rec, String mes) {
        super(own, rec, mes);
        iterationForThisExp=10;
        progress=Progress.NOT_SENDED;
        type="InfPhy";
    }
    @Override
    public Task makeTask(Agent own, Agent rec, String mes) {
        return new InfPhyTask(own, rec, mes);
    }

    @Override
    public void onGetMessage(Message msg) {
        if (feedback(msg)){
            return;
        }
        /*for (int i=0; i<main.tasks.size();i++) {
            System.out.println(((InfPhyTask)main.tasks.get(i)).getProgress()+" "+main.tasks.size());
        }*/
        InfPhyTask task=(InfPhyTask)msg.getTask();
        if (msg.getTarget().isLead()){
            switch (task.getProgress()){
                case SENDED_TO_LEAD:
                    task.setProgress(Progress.WAITING_FOR_OTHERS);
                    if (chekTasks(Progress.WAITING_FOR_OTHERS)){
                        Agent phyLead=Clusterator.getClusters().get(1).getLeader();
                        for (int i=0; i<main.tasks.size();i++){
                            InfPhyTask t=(InfPhyTask)main.tasks.get(i);
                            Message m=new Message(t.message,null,phyLead,msg.getTarget(),t.recipient);
                            m.setTask(t);
                            if (msg.getTarget().isSaboteur()){
                                t.broken_message=true;
                            }
                            if (t.broken_message){
                                m.setNegative(true);
                            }
                            msg.getTarget().sendMessage(phyLead,m);
                            t.progress=Progress.SENDED_TO_PHY;
                        }
                    }
                    break;
                case SENDED_TO_PHY:
                    task.progress=Progress.WAITING_FOR_OTHERS2;
                    if (chekTasks(Progress.WAITING_FOR_OTHERS2)) {
                        for (int i=0;i<main.tasks.size();i++) {
                            InfPhyTask t=(InfPhyTask) main.tasks.get(i);
                            if (t.recipient==msg.getTarget()){
                                t.progress=Progress.LINE_FEED_BACK_WAITING_FOR_OTHER;
                            } else {
                                Message m = new Message(t.message, null, t.recipient,
                                        msg.getTarget());
                                m.setTask(t);
                                if (msg.getTarget().isSaboteur()){
                                    t.broken_message=true;
                                }
                                if (t.broken_message){
                                    m.setNegative(true);
                                }
                                msg.getTarget().sendMessage(t.recipient, m);
                                t.setProgress(Progress.SENDED_TO_RECIPIENT);
                            }
                        }
                    }
                    break;
                case LINE_FEED_BACK_TO_PHY_LEAD:
                    task.setProgress(Progress.LINE_FEED_BACK_WAITING_FOR_OTHER);
                    if (chekTasks(Progress.LINE_FEED_BACK_WAITING_FOR_OTHER)){
                        Agent infLead=Clusterator.getClusters().get(0).getLeader();
                        for (int i=0; i<main.tasks.size();i++){
                            InfPhyTask t=(InfPhyTask)main.tasks.get(i);
                            Message m=new Message("LINE FEEDBACK",null,infLead,msg.getTarget(),t.agOwner);
                            m.setTask(t);
                            if (msg.getTarget().isSaboteur()){
                                t.broken_message=true;
                            }
                            if (t.broken_message){
                                m.setNegative(true);
                            }
                            msg.getTarget().sendMessage(infLead,m);
                            t.progress=Progress.LINE_FEED_BACK_TO_INF_LEAD;
                        }
                    }
                    break;
                case LINE_FEED_BACK_TO_INF_LEAD:
                    Agent infLead=Clusterator.getClusters().get(0).getLeader();
                    task.setProgress(Progress.LINE_FEED_BACK_WAITING_FOR_OTHER2);
                    if (chekTasks(Progress.LINE_FEED_BACK_WAITING_FOR_OTHER2)){
                        for (int i=0; i<main.tasks.size();i++){
                            InfPhyTask t=(InfPhyTask)main.tasks.get(i);
                            if (t.agOwner==infLead){
                                t.progress=Progress.SOLVED;
                                if (t.broken_message){
                                    infLead.setSaboteur(true);
                                }
                                t.solve();
                            } else {
                                Message m = new Message("LINE FEEDBACK", null, t.agOwner, infLead, t.agOwner);
                                m.setTask(t);
                                if (msg.getTarget().isSaboteur()){
                                    t.broken_message=true;
                                }
                                if (t.broken_message){
                                    m.setNegative(true);
                                }
                                infLead.sendMessage(t.agOwner, m);
                                t.progress = Progress.LINE_FEED_BACK_TO_SENDER;
                            }
                        }
                    }

            }
        } else {
            switch (task.progress){
                case LINE_FEED_BACK_TO_SENDER:
                    if (msg.isNegative()){
                        task.agOwner.setSaboteur(true);
                    }
                    task.progress=Progress.SOLVED;
                    task.solve();
                    break;
                case SENDED_TO_RECIPIENT:
                    if (msg.isNegative()){
                        task.recipient.addWrongActionNum();
                    }
                    task.progress=Progress.LINE_FEED_BACK_TO_PHY_LEAD;
                    Message m=new Message("LINE FEED_BACK", null,Clusterator.getClusters().get(1).getLeader(),
                            msg.getTarget());
                    m.setTask(task);
                    if (msg.getTarget().isSaboteur()){
                        ((InfPhyTask) msg.getTask()).broken_message=true;
                    } else {
                        ((InfPhyTask) msg.getTask()).broken_message=false;
                    }
                    if (((InfPhyTask) msg.getTask()).broken_message){
                        m.setNegative(true);
                    }
                    msg.getTarget().sendMessage(Clusterator.getClusters().get(1).getLeader(),m);
            }
        }
    }
    @Override
    public void step() {
        if (agOwner.isSaboteur()){
            this.broken_message=true;
        }
        if (!agOwner.isLead()) {
            Agent a = Clusterator.getClusters().get(0).getLeader();
            Message m = new Message(message, null, a, agOwner, recipient);
            m.setTask(this);
            agOwner.sendMessage(a, m);
            agOwner.removeTask(this);
            this.progress = Progress.SENDED_TO_LEAD;
        } else {
            agOwner.removeTask(this);
            progress=Progress.WAITING_FOR_OTHERS;
        }
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress status) {
        this.progress = status;
    }

    public int getIterationNum() {
        return iterationNum;
    }

    public void setIterationNum(int iterationNum) {
        this.iterationNum = iterationNum;
    }

    public void nextIter(){
        iterationNum++;
    }

    private boolean chekTasks(Progress progress){
        boolean f=true;
        for (int i=0; i< main.tasks.size();i++){
            InfPhyTask t= ((InfPhyTask)main.tasks.get(i));
            if (t.progress!=progress){
                f=false;
                break;
            }
        }
        return f;
    }

    public void newCenterInf(Agent a){
        if (!unique_center_inf.contains(a)){
            unique_center_inf.add(a);
        }
    }
    public void newCenterPhy(Agent a){
        if (!unique_center_phy.contains(a)){
            unique_center_phy.add(a);
        }
    }

    public int getUniqueCenterNumInf(){
        return unique_center_inf.size();
    }

    public int getUniqueCenterNumPhy(){
        return unique_center_phy.size();
    }

    public int getIterationForThisExp() {
        return iterationForThisExp;
    }

    public void setIterationForThisExp(int iterationForThisExp) {
        this.iterationForThisExp = iterationForThisExp;
    }

    enum Progress{
        SENDED_TO_LEAD, NOT_SENDED, WAITING_FOR_OTHERS, SENDED_TO_PHY,WAITING_FOR_OTHERS2, SENDED_TO_RECIPIENT,
        LINE_FEED_BACK_TO_PHY_LEAD, LINE_FEED_BACK_WAITING_FOR_OTHER, LINE_FEED_BACK_TO_INF_LEAD,
        LINE_FEED_BACK_WAITING_FOR_OTHER2, LINE_FEED_BACK_TO_SENDER, SOLVED
    }
}
