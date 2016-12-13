import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class Agent {
    private int id;
    private Point pos;
    private Color color;
    private int R;
    private Queue q;
    private ArrayList<Agent> Connected;

    public Agent()
    {
        Random rnd = StRandom.getR();
        this.Connected= new ArrayList<Agent>();
        int x = rnd.nextInt(CONST.width);
        int y = rnd.nextInt(CONST.height);
        this.setPos(new Point(x, y));
        this.setColor(Color.BLUE);
        this.setR(CONST.R);
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Color c)
    {
        Random rnd = StRandom.getR();
        this.Connected= new ArrayList<Agent>();
        int x = rnd.nextInt(CONST.width);
        int y = rnd.nextInt(CONST.height);
        this.setPos(new Point(x, y));
        this.setColor(c);
        this.setR(CONST.R);
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Point p)
    {
        this.setPos(p);
        this.Connected= new ArrayList<Agent>();
        this.setColor(CONST.color);
        this.setR(CONST.R);
        Random rnd = StRandom.getR();
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(int r)
    {
        Random rnd = StRandom.getR();
        this.Connected= new ArrayList<Agent>();
        int x = rnd.nextInt(CONST.width);
        int y = rnd.nextInt(CONST.height);
        this.setPos(new Point(x, y));
        this.setColor(CONST.color);
        this.setR(r);
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Point p, Color c)
    {
        this.setPos(p);
        this.Connected= new ArrayList<Agent>();
        this.setColor(c);
        this.setR(CONST.R);
        Random rnd = StRandom.getR();
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Color c, int r)
    {
        Random rnd = StRandom.getR();
        this.Connected= new ArrayList<Agent>();
        int x = rnd.nextInt(CONST.width);
        int y = rnd.nextInt(CONST.height);
        this.setPos(new Point(x, y));
        this.setColor(c);
        this.setR(r);
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Point p, int r)
    {
        this.setPos(p);
        this.Connected= new ArrayList<Agent>();
        this.setColor(CONST.color);
        this.setR(r);
        Random rnd = StRandom.getR();
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public Agent(Point p, Color c, int r)
    {
        this.setPos(p);
        this.Connected= new ArrayList<Agent>();
        this.setColor(c);
        this.setR(r);
        Random rnd = StRandom.getR();
        this.setId(rnd.nextInt(CONST.MAXID));
        this.q = new Queue(this);
    }

    public void setPos(Point p)
    {
        this.pos = p;
    }

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

    public ArrayList<Agent> getConnected() { return this.Connected; }

    public void setConnected(ArrayList<Agent> agents)
    {
        this.Connected = agents;
    }

    public boolean addConnected(Agent a)
    {
        if (this.Connected.indexOf(a) != -1) {
            this.Connected.add(a);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeConnected(Agent a)
    {
        if (this.Connected.indexOf(a) != -1) {
            this.Connected.remove(this.Connected.indexOf(a));
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
    public boolean equals(Object o)
    {
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
    public int hashCode()
    {
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
        return (int)Math.round(msg.getContent().length() * CONST.LENKOEF);
    }

    private int getDelayFromLenWithAnalyze(Message msg)
    {
        return (int)Math.round(msg.getContent().length() * CONST.ANALYZKOEF);
    }

    private int getDelayFromDist(Point p)
    {
        double dist = Math.sqrt(Math.pow(p.getX() + this.getPos().getX(), 2) - Math.pow(p.getX() + this.getPos().getX(), 2));
        return (int)Math.round(dist * CONST.DISTKOEF);
    }

    public void sendMessage(final Agent a, final Message msg)
    {
        int delayOnGenerate = getDelayFromLen(msg);
        int delayOnSending = getDelayFromDist(a.getPos());
        this.q.addToQueue(a, delayOnGenerate + delayOnSending, "SEND", msg);

        /*final Agent currentAgent = this;
        main.logging("SEND message: " + msg + " from Agent " + currentAgent.getId() + " to Agent " + a.getId());
        main.fr.addLine(a.getPos(), currentAgent.getPos(), Color.RED);
        a.getMessage(currentAgent, msg);*/
    }

    public void getMessage(final Agent a, final Message msg)
    {
        main.logging(this.getId() + " GET MESSAGE FROM " + a.getId() + ": " + msg);
        if (msg.getContent().equals(CONST.READMSG))
        {
            main.logging(this.getId() + " GET CONFIRMATION MESSAGE  FROM " + a.getId());
            main.fr.removeLine(a.getPos(), this.getPos());
            //main.logging("LINE " + a.getPos() + " " + this.getPos() + " REMOVED");
            return;
        }

        this.q.addToQueue(a, getDelayFromLenWithAnalyze(msg), "GET", msg);
    }
}
