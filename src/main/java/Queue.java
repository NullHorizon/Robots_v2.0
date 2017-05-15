import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by AsmodeusX on 30.11.2016.
 */
public class Queue {

    static class messageData {
        private Agent agent;
        private int delay;
        private String type;
        private Message msg;

        public messageData(Agent a, int b, String t, Message c)
        {
            this.agent = a;
            this.delay = b;
            this.type = t;
            this.msg = c;
        }

        public Agent getAgent()
        {
            return this.agent;
        }

        public void setAgent(Agent a)
        {
            this.agent = a;
        }

        public int getDelay()
        {
            return this.delay;
        }

        public void setDElay(int b)
        {
            this.delay = b;
        }

        public String getType()
        {
            return this.type;
        }

        public void setType(String a)
        {
            this.type = a;
        }

        public Message getMessage()
        {
            return this.msg;
        }

        public void setMessage(Message a)
        {
            this.msg = a;
        }
    }

    private Vector<messageData> elements;
    private boolean working;
    private Agent owner;

    public Queue(Agent a)
    {
        this.elements = new Vector<messageData>();
        this.owner = a;
        Work();
    }

    public void addToQueue(Agent a, int delay, String type, Message msg)
    {
        if (this.elements.isEmpty()) {
            this.elements.add(new messageData(a, delay, type, msg));
            Work();
        } else {
            this.elements.add(new messageData(a, delay, type, msg));
        }
    }

    public void removeFromQueue(final messageData p)
    {
        //precessing animation
        if (p.getType() == "SEND") {
            main.logging(owner.getId() + " SENT MESSAGE " + p.getMessage().getContent() + " TO " + p.getAgent().getId());
            p.getAgent().getMessage(owner, p.getMessage());
        } else
        {
            main.logging(owner.getId() + " READ MESSAGE " + p.getMessage().getContent() + " FROM " + p.getAgent().getId());
            //owner.sendMessage(p.getAgent(), new Message(CONST.READMSG, null, p.getAgent(), owner));
            main.fr.removeLine(owner.getPos(), p.getAgent().getPos());
        }
        this.elements.remove(p);
        //main.logging(this.toString());
    }

    public Agent nowInWork()
    {
        if (this.elements.isEmpty()) {
            return null;
        }
        return this.elements.firstElement().getAgent();
    }

    public int getLength() {
        return this.elements.size();
    }

    private void Work()
    {
        if (this.elements.isEmpty()) {
            working=false;
            return;
        }
        working=true;

        final messageData firstElement = this.elements.firstElement();

        if (firstElement.getType().equals("GET")) {
            main.logging(owner.getId() + " READING MESSAGE " + firstElement.getMessage().getContent() + " FROM " + firstElement.getAgent().getId());
        } else {
            main.logging(owner.getId() + " SENDING MESSAGE " + firstElement.getMessage().getContent() + " TO " + firstElement.getAgent().getId());
        }
        main.fr.addLine(owner.getPos(), firstElement.getAgent().getPos(), Color.red);

        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                this.cancel();
                removeFromQueue(firstElement);
                Work();
            }
        }, firstElement.getDelay());
    }

    @Override
    public String toString()
    {
        String ans = "Elements in queue: ";
        for(messageData p : this.elements)
        {
            ans += (" " + p.toString());
        }
        return ans;
    }

    public boolean isWorking() {
        return working;
    }
}
