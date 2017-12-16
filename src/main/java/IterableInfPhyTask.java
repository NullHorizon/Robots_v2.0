import java.util.ArrayList;

public class IterableInfPhyTask extends Task {
    @Override
    public Task makeTask(Agent own, Agent rec, String mes) {
        return new IterableInfPhyTask(own,rec,mes);
    }

    private static int iterationNum=1;
    private int infConfirmationNum;
    private Progress progress;
    private boolean isPlanBroken, isFeedbackBroken;
    private static int iterationForThisExp,
        subGroups;

    public IterableInfPhyTask(){
        type="IterableInfPhy";
    }

    public IterableInfPhyTask(Agent own, Agent rec, String mes) {
        super(own, rec, mes);
        isPlanBroken=false;
        isFeedbackBroken=false;
        type="IterableInfPhy";
    }

    @Override
    public void onGetMessage(Message msg) {
        Progress taskProgress=((IterableInfPhyTask) msg.getTask()).progress;
        if (msg.getTarget().isLead()){
            switch (taskProgress){
                case SENDED_TO_INF_GROUP_LEAD: tryToStartConfirmation(msg); break;
                case INF_CONFIRMATION_IN_PROGRESS: tryToStartSendingToPhy(msg); break;
                case SENDED_TO_PHY_GROUP_LEAD: sendToWorker(msg); break;
                case SENDED_FEEDBACK_TO_PHY_GROUP_LEAD: tryToSendFeedbackToInf(msg); break;
                case SENDED_FEEDBACK_TO_INF_GROUP_LEAD: infFeedBackConfirmation(msg); break;
                case SEND_FEED_BACK_TO_OTHER_LEADERS_IN_PROGRESS: tryToSendFeedbackToInfElements(msg); break;
            }
        } else {
            switch (taskProgress){
                case SENDED_TO_PHY_ELEMENT: tryToStartFeedbackProcess(msg); break;
                case SENDED_FEEDBACK_TO_INF_ELEMENT: tryToComplete(msg); break;
            }
        }
    }

    @Override
    public void step() {
        if (agOwner.isSaboteur()){
            isPlanBroken=true;
        }
        if (agOwner.isLead()){
            progress=Progress.WAITING_FOR_OTHERS_TO_START_CONFIRMATION;
            agOwner.removeTask(this);
            if (checkTasksInGroup(Progress.WAITING_FOR_OTHERS_TO_START_CONFIRMATION, agOwner)){
                for (Agent a: getAllLeaders(Agent.LogicType.INF)){
                    if (!a.equals(agOwner)) {
                        Message newMessage = new Message(message, null, a, agOwner,recipient);
                        this.infConfirmationNum=0;
                        newMessage.setTask(this);
                        agOwner.sendMessage(a,newMessage);
                    }
                }
                setNewStatusForAllInGroup(Progress.INF_CONFIRMATION_IN_PROGRESS, agOwner);
            }
        } else {
            Agent infLead=getLeaderForAgent(agOwner, Agent.LogicType.INF);
            Message m=new Message(message,null,infLead,agOwner,recipient);
            m.setTask(this);
            progress=Progress.SENDED_TO_INF_GROUP_LEAD;
            agOwner.sendMessage(infLead,m);
            agOwner.removeTask(this);
        }
    }

    public int getIterationNum() {
        return iterationNum;
    }

    public static void setIterationNum(int iterationNum) {
        IterableInfPhyTask.iterationNum = iterationNum;
    }

    public void nextIter(){
        System.out.println("NEXT ITER");
        iterationNum++;
        if (Params.RECLUSTERISATION) {
            Clusterator.setClNum(subGroups);
            Clusterator.iterableInfPhyClusterisation();
        }
    }

    public int getIterationForThisExp() {
        return iterationForThisExp;
    }

    public int getSubGroups() {
        return subGroups;
    }


    private Agent getLeaderForAgent(Agent a,Agent.LogicType logicType){
        Clusterator.Cluster cluster=null;
        for (Clusterator.Cluster c:Clusterator.getClusters()){
            if (c.getAgents().contains(a)){
                cluster=c;
                break;
            }
        }
        for (Agent agent: cluster.getAgents()) {
            if (agent.getLogicType() == logicType && agent.isLead()) {
                return agent;
            }
        }
        return null;
    }

    private void tryToStartConfirmation(Message msg){
        IterableInfPhyTask confirmationTask=(IterableInfPhyTask) msg.getTask();
        confirmationTask.progress=Progress.WAITING_FOR_OTHERS_TO_START_CONFIRMATION;
        if (checkTasksInGroup(Progress.WAITING_FOR_OTHERS_TO_START_CONFIRMATION, confirmationTask.agOwner)){
            for (Agent a: getAllLeaders(Agent.LogicType.INF)){
                if (!a.equals(msg.getTarget())) {
                    Message newMessage = new Message(confirmationTask.message, null, a, msg.getTarget()
                            ,confirmationTask.recipient);
                    confirmationTask.infConfirmationNum=0;
                    newMessage.setTask(msg.getTask());
                    if (msg.getTarget().isSaboteur()){
                        for (IterableInfPhyTask it : getTasksForGroupOfAgent(confirmationTask.recipient)) {
                            it.isPlanBroken = true;
                        }
                    }
                    msg.getTarget().sendMessage(a,newMessage);
                }
            }
            setNewStatusForAllInGroup(Progress.INF_CONFIRMATION_IN_PROGRESS, confirmationTask.agOwner);
        }
    }

    private void tryToStartSendingToPhy(Message msg){
        IterableInfPhyTask confirmationTask=((IterableInfPhyTask) msg.getTask());
        confirmationTask.infConfirmationNum++;
        if (confirmationTask.infConfirmationNum==((IterableInfPhyTask)Simulator.taskType).subGroups-1) {
            setNewStatusForAllInGroup(Progress.INF_CONFIRMATION_COMPLETED, confirmationTask.agOwner);
        }
        if (checkAllTasks(Progress.INF_CONFIRMATION_COMPLETED)){
            for (Agent a: getAllLeaders(Agent.LogicType.INF)){
                Agent phyLead= getLeaderForAgent(a, Agent.LogicType.PHY);
                Message newMessage=new Message("COMPLEX SEND TO PHY LEAD", null, phyLead, a, phyLead);
                Task complexTask=null;
                for (Task t:Simulator.tasks){
                    if (t.agOwner.equals(a)){
                        complexTask=t;
                        break;
                    }
                }
                newMessage.setTask(complexTask);
                if (a.isSaboteur()){
                    for (IterableInfPhyTask it:getTasksForGroupOfAgent(a)){
                        it.isPlanBroken=true;
                    }
                }
                a.sendMessage(phyLead,newMessage);
            }
            setNewStatusForAll(Progress.SENDED_TO_PHY_GROUP_LEAD);
        }
    }

    private void sendToWorker(Message msg){
        IterableInfPhyTask complexTask=((IterableInfPhyTask) msg.getTask());
        Agent phyLead=msg.getTarget();
        for (IterableInfPhyTask task: getTasksForGroupOfAgent(phyLead)){
            if (task.recipient.equals(phyLead)){
                if (task.recipient.isSaboteur()){
                    task.isFeedbackBroken=true;
                }
                if (task.isPlanBroken){
                    task.recipient.addWrongActionNum();
                }
                task.progress=Progress.WAITING_FOR_WORK_COMPLETE;
                if (checkTasksInGroup(Progress.WAITING_FOR_WORK_COMPLETE,phyLead)){
                    sendPhyFeedback(phyLead);
                }
                continue;
            }
            Message newMessage=new Message(task.message,null,task.recipient,phyLead,task.recipient);
            newMessage.setTask(task);
            phyLead.sendMessage(task.recipient,newMessage);
            task.progress=Progress.SENDED_TO_PHY_ELEMENT;
        }
    }

    private void tryToStartFeedbackProcess(Message msg){
        IterableInfPhyTask task=((IterableInfPhyTask) msg.getTask());
        task.progress=Progress.WAITING_FOR_WORK_COMPLETE;
        if (task.isPlanBroken){
            task.recipient.addWrongActionNum();
        }
        if (task.recipient.isSaboteur()){
            task.isFeedbackBroken=true;
        }
        if (checkTasksInGroup(Progress.WAITING_FOR_WORK_COMPLETE,task.recipient)){
            sendPhyFeedback(getLeaderForAgent(task.recipient, Agent.LogicType.PHY));
        }
    }

    private void tryToSendFeedbackToInf(Message msg){
        IterableInfPhyTask task=((IterableInfPhyTask) msg.getTask());
        task.progress=Progress.WAITING_FOR_OTHERS_TO_SEND_FEEDBACK_TO_INF;
        if (checkTasksInGroup(Progress.WAITING_FOR_OTHERS_TO_SEND_FEEDBACK_TO_INF, msg.getTarget())){
            Agent infLead=getLeaderForAgent(msg.getTarget(), Agent.LogicType.INF),
                    phyLead=getLeaderForAgent(msg.getTarget(), Agent.LogicType.PHY);
            Message newMessage=new Message("complex_feedback_to_inf",null,infLead,phyLead,infLead);
            newMessage.setTask(task);
            if (phyLead.isSaboteur()){
                for (IterableInfPhyTask it: getTasksForGroupOfAgent(phyLead)){
                    it.isFeedbackBroken=true;
                }
            }
            setNewStatusForAllInGroup(Progress.SENDED_FEEDBACK_TO_INF_GROUP_LEAD, msg.getTarget());
            phyLead.sendMessage(infLead,newMessage);
        }
    }

    private void infFeedBackConfirmation(Message msg){
        IterableInfPhyTask confirmationTask=((IterableInfPhyTask) msg.getTask());
        confirmationTask.infConfirmationNum=0;
        for (Agent agent:getAllLeaders(Agent.LogicType.INF)){
            if (!agent.equals(msg.getTarget())){
                Message newMessage=new Message("confirmation_of_feedback_with_others",null,agent,msg.getTarget(),agent);
                newMessage.setTask(confirmationTask);
                if (msg.getTarget().isSaboteur()){
                    for (IterableInfPhyTask it: getTasksForGroupOfAgent(msg.getTarget())){
                        it.isFeedbackBroken=true;
                    }
                }
                msg.getTarget().sendMessage(agent,newMessage);
            }
        }
        setNewStatusForAllInGroup(Progress.SEND_FEED_BACK_TO_OTHER_LEADERS_IN_PROGRESS,msg.getTarget());
    }

    private void tryToSendFeedbackToInfElements(Message msg){
        IterableInfPhyTask confirmationTask=((IterableInfPhyTask) msg.getTask());
        confirmationTask.infConfirmationNum++;
        if (confirmationTask.isFeedbackBroken){
            msg.getTarget().setSaboteur(true);
        }
        if (confirmationTask.infConfirmationNum==((IterableInfPhyTask)Simulator.taskType).subGroups-1){
            setNewStatusForAllInGroup(Progress.WAITING_FOR_OTHER_FEED_BACK,msg.getFrom());
        }
        if (checkAllTasks(Progress.WAITING_FOR_OTHER_FEED_BACK)){
            for (Task t: Simulator.tasks){
                IterableInfPhyTask task=(IterableInfPhyTask)t;
                if (task.agOwner.isLead()) {
                    if (task.isFeedbackBroken){
                        task.agOwner.setSaboteur(true);
                    }
                    task.progress = Progress.COMPLETED;
                    task.progress=Progress.COMPLETED;
                    task.solve();
                }
                Agent infLead=getLeaderForAgent(task.agOwner, Agent.LogicType.INF);
                Message newMessage=new Message(task.message+"_feedback", null, task.agOwner,infLead,task.agOwner);
                newMessage.setTask(task);
                infLead.sendMessage(task.agOwner,newMessage);
                task.progress=Progress.SENDED_FEEDBACK_TO_INF_ELEMENT;
            }
        }
    }

    private void tryToComplete(Message msg){
        IterableInfPhyTask task=((IterableInfPhyTask) msg.getTask());
        if (task.isFeedbackBroken){
            task.agOwner.setSaboteur(true);
        }
        task.progress=Progress.COMPLETED;
        task.solve();
    }

    private boolean checkTasksInGroup(Progress progressForCheck, Agent a){
        for (IterableInfPhyTask t: getTasksForGroupOfAgent(a)){
            if (t.progress!=progressForCheck){
                return false;
            }
        }
        return true;
    }

    private boolean checkAllTasks(Progress progressForCheck){
        for (Task t: Simulator.tasks){
            IterableInfPhyTask task=(IterableInfPhyTask) t;
            if (task.progress!=progressForCheck){
                return false;
            }
        }
        return true;
    }

    private ArrayList<Agent> getAllLeaders(Agent.LogicType logicType){
        ArrayList<Agent> answer=new ArrayList<>();
        for (Agent a: Simulator.getAgents()){
            if (a.isLead() && a.getLogicType()==logicType){
                answer.add(a);
            }
        }
        return answer;
    }

    private void setNewStatusForAllInGroup(Progress newProgress, Agent a){
        for (IterableInfPhyTask t: getTasksForGroupOfAgent(a)) {
            t.progress=newProgress;
        }
    }

    private ArrayList<IterableInfPhyTask> getTasksForGroupOfAgent(Agent a){
        ArrayList<IterableInfPhyTask> answer=new ArrayList<>();
        ArrayList<Agent> groupAgents=null;
        for (Clusterator.Cluster cluster:Clusterator.getClusters()){
            if (cluster.getAgents().contains(a)){
                groupAgents=cluster.getAgents();
                break;
            }
        }
        for (Task t:Simulator.tasks){
            if (groupAgents.contains(t.agOwner)){
                answer.add((IterableInfPhyTask) t);
            }
        }
        return answer;
    }

    private void setNewStatusForAll(Progress newProgress){
        for (Task t:Simulator.tasks){
            ((IterableInfPhyTask) t).progress=newProgress;
        }
    }

    private void sendPhyFeedback(Agent phyLead){
        for (IterableInfPhyTask task:getTasksForGroupOfAgent(phyLead)){
            if (task.recipient.equals(phyLead)){
                task.progress=Progress.WAITING_FOR_OTHERS_TO_SEND_FEEDBACK_TO_INF;
                continue;
            }
            Message newMessage=new Message(task.message+"_feedback",null,phyLead,task.recipient,agOwner);
            newMessage.setTask(task);
            task.recipient.sendMessage(phyLead,newMessage);
            task.progress=Progress.SENDED_FEEDBACK_TO_PHY_GROUP_LEAD;
        }
    }

    public void setSubGroups(int subGroups) {
        this.subGroups = subGroups;
    }

    public static void setIterationForThisExp(int iterationForThisExp) {
        IterableInfPhyTask.iterationForThisExp = iterationForThisExp;
    }


    enum Progress{
        SENDED_TO_INF_GROUP_LEAD,
        WAITING_FOR_OTHERS_TO_START_CONFIRMATION,
        INF_CONFIRMATION_IN_PROGRESS,
        INF_CONFIRMATION_COMPLETED,
        SENDED_TO_PHY_GROUP_LEAD,
        SENDED_TO_PHY_ELEMENT,
        WAITING_FOR_WORK_COMPLETE,
        SENDED_FEEDBACK_TO_PHY_GROUP_LEAD,
        WAITING_FOR_OTHERS_TO_SEND_FEEDBACK_TO_INF,
        SENDED_FEEDBACK_TO_INF_GROUP_LEAD,
        SEND_FEED_BACK_TO_OTHER_LEADERS_IN_PROGRESS,
        WAITING_FOR_OTHER_FEED_BACK,
        SENDED_FEEDBACK_TO_INF_ELEMENT,
        COMPLETED
    }
}
