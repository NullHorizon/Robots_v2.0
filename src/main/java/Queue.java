import java.awt.*;
import java.util.Vector;

public class Queue {

    static class messageData {
        private int curDelay;
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
            curDelay=delay;
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
        if (p.getType() == "SEND") {
            Simulator.logging(owner.getId() + " SENT MESSAGE " + p.getMessage().getContent() + " TO " + p.getAgent().getId());
            p.getAgent().getMessage(owner, p.getMessage());
        } else
        {
            Simulator.logging(owner.getId() + " READ MESSAGE " + p.getMessage().getContent() + " FROM " + p.getAgent().getId());
            Simulator.fr.removeLine(owner.getPos(), p.getAgent().getPos());
        }
        this.elements.remove(p);
    }

    public messageData nowInWork()
    {
        if (this.elements.isEmpty()) {
            return null;
        }
        return this.elements.firstElement();
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
            Simulator.logging(owner.getId() + " READING MESSAGE " + firstElement.getMessage().getContent() + " FROM " + firstElement.getAgent().getId());
        } else {
            Simulator.logging(owner.getId() + " SENDING MESSAGE " + firstElement.getMessage().getContent() + " TO " + firstElement.getAgent().getId());
        }
        Simulator.fr.addLine(owner.getPos(), firstElement.getAgent().getPos(), Color.red);
//
//        Timer tmr = new Timer();
//        tmr.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                this.cancel();
//                removeFromQueue(firstElement);
//                Work();
//            }
//        }, firstElement.getDelay());
    }

    public void step(){
        if (nowInWork()!=null){
            nowInWork().curDelay--;
            if (nowInWork().curDelay<=0){
                removeFromQueue(nowInWork());
                Work();
            }
        }
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
