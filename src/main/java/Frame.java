
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by shepkan on 30.11.2016.
 */
public class Frame extends JFrame implements ActionListener {

    private JProgressBar progressBar;
    private JPanel but_panel, robots_panel, cluster_panel, que_panel ;
    private final int w=CONST.width+200+60, h=CONST.height+60;
    private JRadioButton que1_butt, que2_butt, que3_butt,cluster1_butt, cluster2_butt, cluster3_butt;
    private JButton someButt;
    private ArrayList<Line> lines;

    public Frame(){
        super("Satan caller");
        BufferedImage icon = null;//new ImageIcon("./src/resources/icon.gif");
        try {
            icon = ImageIO.read(getClass().getResource("/resources/icon.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIconImage(icon);
        setSize(w,h);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        lines=new ArrayList<Line>();

        but_panel=new JPanel();
        but_panel.setSize(200,h);
        but_panel.setBounds(0,0,200,h);
        but_panel.setLayout(new FlowLayout());

        someButt= new JButton("Stat button");
        someButt.addActionListener(this);
        but_panel.add(someButt);

        progressBar=new JProgressBar(0,CONST.EXPERIMENT_NUM);
        but_panel.add(progressBar);

        cluster_panel=new JPanel();
        cluster_panel.setBorder(BorderFactory.createLoweredBevelBorder());
        cluster_panel.setLayout(new GridLayout(4,1));
        cluster_panel.add(new JLabel("Cluster type:"));
        ButtonGroup cluster_group= new ButtonGroup();
        cluster1_butt=new JRadioButton("No cluster", true);
        cluster2_butt=new JRadioButton("Simple cluster", false);
        cluster3_butt= new JRadioButton("Leader cluster", false);
        cluster_group.add(cluster1_butt);
        cluster_group.add(cluster2_butt);
        cluster_group.add(cluster3_butt);
        cluster_panel.add(cluster1_butt);
        cluster_panel.add(cluster2_butt);
        cluster_panel.add(cluster3_butt);
        //but_panel.add(cluster_panel);

        que_panel=new JPanel();
        que_panel.setBorder(BorderFactory.createLoweredBevelBorder());
        que_panel.setLayout(new GridLayout(4,1));
        que_panel.add(new JLabel("Que type:"));
        ButtonGroup que_group= new ButtonGroup();
        que1_butt=new JRadioButton("Que type 1", true);
        que2_butt=new JRadioButton("Que type 2", false);
        que3_butt= new JRadioButton("Que type 3", false);
        que_group.add(que1_butt);
        que_group.add(que2_butt);
        que_group.add(que3_butt);
        que_panel.add(que1_butt);
        que_panel.add(que2_butt);
        que_panel.add(que3_butt);
        //but_panel.add(que_panel);


        add(but_panel);

        robots_panel=new RobotsPanel(w-200,h);
        robots_panel.setBounds(200,0,w,h);
        add(robots_panel);

        setVisible(true);
    }

    public void reset(){
        lines=new ArrayList<Line>();
        progressBar.setValue(main.cur_exp);
        repaint();
    }

    public synchronized void addLine(Point p1, Point p2, Color color){
        if (lines.indexOf(new Line(p1,p2))==-1) {
            lines.add(new Line(p1, p2, color));
            this.repaint();
        }
    }

    public synchronized void addLine(Point p1, Point p2){
        if (lines.indexOf(new Line(p1,p2))==-1){
            lines.add(new Line(p1, p2));
            this.repaint();
        }
    }

    public synchronized void  removeLine(Point p1, Point p2){
        for (int i=0; i<lines.size();i++){
            Line l=lines.get(i);
            if (new Line(p1, p2).equals(l)) {
                lines.remove(l);
            }
        }
        this.repaint();
    }

    public int getQueType(){
        if (que1_butt.isSelected()){
            return 1;
        }
        if (que1_butt.isSelected()){
            return 2;
        }
        if (que1_butt.isSelected()){
            return 3;
        }
        return 0;
    }

    public int getClusterType(){
        if (cluster1_butt.isSelected()){
            return 1;
        }
        if (cluster2_butt.isSelected()){
            return 2;
        }
        if (cluster3_butt.isSelected()){
            return 3;
        }
        return 0;
    }


    public void actionPerformed (ActionEvent e) {

        switch (e.getActionCommand()){
            case "Stat button": Table.showTable();  break;
        }
    }

    private class RobotsPanel extends JPanel {
        private int w,h;
        public RobotsPanel(int w, int h){
            super();
            this.w=w;
            this.h=h;
            setSize(w, h);
            setBackground(Color.WHITE);
        }

        @Override
        public synchronized  void paint(Graphics g){
            g.setColor(Color.WHITE);
            g.fillRect(0,0,w,h);
            for (int i=0; i<lines.size();i++) {
                lines.get(i).draw(g);
            }
            for (int i=0; i<main.agents.size();i++){
                Agent a=main.agents.get(i);
                g.setColor(a.getColor());
                int r=a.getR();
                g.fillOval(a.getPos().x-r+25,a.getPos().y-r+25,r*2,r*2);
                if (a.isBroken()) {
                    g.setColor(Color.BLACK);
                    g.drawLine(a.getPos().x - r+25, a.getPos().y-r+25,a.getPos().x + r+25, a.getPos().y+r+25);
                    g.drawLine(a.getPos().x + r+25, a.getPos().y-r+25,a.getPos().x - r+25, a.getPos().y+r+25);
                }
                if (a.isSaboteur()){
                    g.setColor(Color.RED);
                    g.drawOval(a.getPos().x-r+25,a.getPos().y-r+25,r*2,r*2);
                }
                if (a.isLead()){
                    g.setColor(Color.orange);
                    g.drawRect(a.getPos().x-r+25,a.getPos().y-r+25,r*2,r*2);
                }
                for (int j=0;j<a.getWrongActionNum();j++){
                    g.setColor(Color.RED);
                    g.fillOval(a.getPos().x+r+j*6+25,a.getPos().y+25,5,5);
                }
            }
            if (main.taskType.getType()=="InfPhy"){
                g.setColor(Color.BLUE);
                g.drawLine(CONST.width/2+25+CONST.R,0,CONST.width/2+CONST.R+25,CONST.height);
            }
        }

    }

    private static class Line{
        Color color;
        Point p1, p2;

        public Line(Point p1, Point p2, Color color){
            this.p1=p1;
            this.p2=p2;
            this.color=color;
        }

        public Line(Point p1, Point p2){
            this.p1=p1;
            this.p2=p2;
            this.color=Color.black;
        }

        public void draw(Graphics g){
            g.setColor(color);
            g.drawLine(p1.x+25, p1.y+25, p2.x+25, p2.y+25);
        }

        @Override
        public boolean equals(Object obj){
            Line l=(Line) obj;
            if ((l.p1 == this.p1 && l.p2 == this.p2) || (l.p1 == this.p2 && l.p2 == this.p1))  {
                return true;
            } else {
                return false;
            }
        }
    }
}
