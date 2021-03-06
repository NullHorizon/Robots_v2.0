import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.awt.Color.MAGENTA;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class Agent {
    private boolean lead=false;
    private int id;
    private Point pos;
    private Color color;
    private int R;
    private Queue q;
    private ArrayList<Agent> connected;
    private ArrayList<Agent> targets=new ArrayList<>();
    private ArrayList<Task> tasks;
    private int clusterId;
    private boolean saboteur=false;
    private boolean broken=false;
    private Agent targeted=null;
    private int wrongActionNum=0;
    private int badCenterTime=0;
    private Timer badCenterTimer;

    public Agent()
    {
        Random rnd = StRandom.getR();
        this.connected= new ArrayList<Agent>();
        int x = rnd.nextInt(Params.width);
        int y = rnd.nextInt(Params.height);
        this.setPos(new Point(x, y));
        this.setColor(Color.BLUE);
        this.setR(Params.R);
        this.setId(rnd.nextInt(Params.MAXID));
        this.q = new Queue(this);
        tasks=new ArrayList<Task>();
        badCenterTimer=new Timer();
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                if (isLead() && isSaboteur()) {
                    badCenterTime += 10;
                }
            }
        };
        badCenterTimer.schedule(tt,10,10);

    }

    public boolean getTargeted() {
        if (targeted==null){
            return false;
        } else {
            return true;
        }
    }

    public void setTargeted(Agent a) {
        targeted=a;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }


    public void setPos(Point p)
    {
        this.pos = p;
    }

    public void setClusterId(int id){ this.clusterId=id;}

    public int getClusterId(){return this.clusterId;}

    public Point getPos()
    {
        return this.pos;
    }

    public void setColor(Color c)
    {
        this.color = c;
    }

    public Color getColor()
    {
        return this.color;
    }

    public void setR(int r)
    {
        this.R = r;
    }

    public int getR()
    {
        return this.R;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isSaboteur(){ return this.saboteur;}
    public void setSaboteur(boolean saboteur){ this.saboteur=saboteur;}

    public ArrayList<Agent> getConnected() { return this.connected; }

    public boolean isLead(){return lead;}
    public void setLead(boolean t){lead=t;}

    public void setConnected(ArrayList<Agent> agents)
    {
        this.connected = agents;
    }

    public void addTask(Task t){
        tasks.add(t);
        if (tasks.size()==1){
            tasks.get(0).step();
        }
    }

    public boolean removeTask(Task t){
        if (this.tasks.indexOf(t)!=-1){
            this.tasks.remove(this.tasks.indexOf(t));
            if (tasks.size()>0){
                tasks.get(0).step();
            }
            return true;
        } else {
            return false;
        }
    }

    public void nextTaskStep(){
        if (tasks.size()>0) {
            tasks.get(0).step();
        }
    }

    public boolean addConnected(Agent a) {
        if (this.connected.indexOf(a) == -1) {
            this.connected.add(a);
            Simulator.fr.addLine(new Point(this.getPos().x+3, this.getPos().y+3),new Point(a.getPos().x+3,a.getPos().y+3),Color.GREEN);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeConnected(Agent a) {
        if (this.connected.indexOf(a) != -1) {
            this.connected.remove(this.connected.indexOf(a));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        //return "Agent with ID: " + this.getId() + " is at Point: " + this.getPos().getX() + "." + this.getPos().getY() + " with R: " + this.getR() + " and has color: " + this.getColor().toString();
        return "ID: " + this.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        Agent a = (Agent) o;
        return (a.getId() == this.getId());
    }

    @Override
    public int hashCode() {
        int hash = 31;
        int koef = 17;
        hash += koef * this.getId();
        hash += koef * this.getR();
        hash += koef * this.getPos().getX();
        hash += koef * this.getPos().getY();
        return hash;
    }

    private int getDelayFromLen(Message msg)
    {
        return (int)Math.round(msg.getContent().length() * Params.LENKOEF);
    }

    private int getDelayFromLenWithAnalyze(Message msg) {
        return (int)Math.round(msg.getContent().length() * Params.ANALYZKOEF);
    }

    private int getDelayFromDist(Point p) {
        double dist = Math.sqrt(Math.pow(p.getX() + this.getPos().getX(), 2) - Math.pow(p.getX() + this.getPos().getX(), 2));
        return (int)Math.round(dist * Params.DISTKOEF);
    }

    public int getBadCenterTime() {
        return badCenterTime;
    }

    public void sendMessage(final Agent a, final Message msg) {
        Simulator.stats.addAllMessages();
        if (msg.isNegative()) {
            Simulator.stats.addBadMessages();
        }
        int delayOnGenerate = getDelayFromLen(msg);
        int delayOnSending = getDelayFromDist(a.getPos());
        this.q.addToQueue(a, delayOnGenerate + delayOnSending, "SEND", msg);

        /*final Agent currentAgent = this;
        Simulator.logging("SEND message: " + msg + " from Agent " + currentAgent.getId() + " to Agent " + a.getId());
        Simulator.fr.addLine(a.getPos(), currentAgent.getPos(), Color.RED);
        a.getMessage(currentAgent, msg);*/
    }

    public void getMessage(final Agent a, final Message msg)
    {
        Simulator.logging(this.getId() + " GET MESSAGE FROM " + a.getId() + ": " + msg);
        if (msg.getContent().equals(Params.READMSG))
        {
            Simulator.logging(this.getId() + " GET CONFIRMATION MESSAGE  FROM " + a.getId());
            Simulator.fr.removeLine(a.getPos(), this.getPos());
            return;
        }

        this.q.addToQueue(a, getDelayFromLenWithAnalyze(msg), "GET", msg);
        Simulator.taskType.onGetMessage(msg);
    }

    public void addTarget(Agent a){
        if (!targets.contains(a)){
            Simulator.fr.addLine(new Point(this.getPos().x+1, this.getPos().y+1),
                    new Point(a.getPos().x,a.getPos().y), MAGENTA);
            this.targets.add(a);
        }
    }

    public ArrayList<Agent> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Agent> targets) {
        this.targets = targets;
    }

    public Queue getQ() {
        return q;
    }
    public void addWrongActionNum(){
        Simulator.stats.addWrongPhyAction();
        wrongActionNum++;
    }

    public int getWrongActionNum() {
        return wrongActionNum;
    }
    public void destory(){
        badCenterTimer.cancel();
    }
}
