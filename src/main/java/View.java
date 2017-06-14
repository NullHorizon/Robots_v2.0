
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * Created by shepkan on 30.11.2016.
 */
public class View extends JFrame {

    private JProgressBar progressBar, iterProgressBar;
    private JPanel but_panel, robots_panel;
    private final int w= Params.width+300+60, h= Params.height+60;
    private JButton stats_but, start_but;
    private ArrayList<Line> lines=new ArrayList<>();
    private JLabel log_string;
    private JRadioButton infPhy_rb, cluster_rb, leader_rb, simple_rb;
    private JTextField agent_min_num, agent_max_num, saboteur_percent_min, saboteur_percent_max,
            message_filter_percent_min, message_filter_percent_max, center_filter_percent_min, center_filter_percent_max,
    len_koef, dist_koef, analys_koef, experiment_num, seed, min_msg_len, max_msg_len, task_num;
    private JCheckBox draw;

    public View(){
        //------------------настройки окна---------------------
        super("Friendly robots");
        setSize(w+50,h+50);
        ImageIcon icon=new ImageIcon("src/resources/icon.gif");
        setIconImage(icon.getImage());
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        //----------------панель кнопок-----------------------------------------------------
        final int panel_w=280;
        but_panel=new JPanel();
        //but_panel.setBounds(0,0,panel_w+20,900);
        but_panel.setPreferredSize(new Dimension(panel_w+20,h*2));
        but_panel.setLayout(new FlowLayout());

        //общий менеджер для панелей
        class CastomPanel extends JPanel{
            CastomPanel(){
                FlowLayout fl=new FlowLayout();
                fl.setAlignment(FlowLayout.LEFT);
                setLayout(fl);
                setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
                setPreferredSize(new Dimension(panel_w,500));
            }
        }

        //поле ввода
        class EntryField extends JTextField{
            EntryField(){
                super();
                addKeyListener(new java.awt.event.KeyAdapter() {
                    public void keyTyped(java.awt.event.KeyEvent e) {
                        char a = e.getKeyChar();
                        if (!Character.isDigit(a)){
                            e.consume();
                        }
                    }
                });
                setText("0");
                setPreferredSize(new Dimension(100,20));
            }
        }

        //старт опытов
        start_but=new JButton("Старт");
        start_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Simulator.go();
            }
        });
        but_panel.add(start_but);

        //вывод результата
        stats_but = new JButton("Результат");
        stats_but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.showTable();
            }
        });
        but_panel.add(stats_but);

        //----------прогрессбар--------------------
        progressBar=new JProgressBar(0, Params.EXPERIMENT_NUM);
        but_panel.add(progressBar);
        iterProgressBar=new JProgressBar(0,10);
        iterProgressBar.setVisible(false);
        but_panel.add(iterProgressBar);

        //------------------------общие настройки-----------------
        JPanel common_panel=new CastomPanel();
        common_panel.setPreferredSize(new Dimension(panel_w,200));
        draw=new JCheckBox("Отрисовка");
        draw.setSelected(true);
        experiment_num=new EntryField();
        experiment_num.setText("100");
        max_msg_len=new EntryField();
        max_msg_len.setText("10");
        min_msg_len=new EntryField();
        min_msg_len.setText("8");
        task_num=new EntryField();
        seed=new EntryField();
        common_panel.add(new JLabel("Количество экспериментов:"));
        common_panel.add(experiment_num);
        common_panel.add(new JLabel("Зерно:"));
        common_panel.add(seed);
        common_panel.add(draw);
        common_panel.add(new JLabel("Минимальная длина сообщения:"));
        common_panel.add(min_msg_len);
        common_panel.add(new JLabel("Максимальная длина сообщения:"));
        common_panel.add(max_msg_len);
        common_panel.add(new JLabel("Количество заданий (агенты/2 если 0):"));
        common_panel.add(task_num);
        but_panel.add(common_panel);

        //---------------------------------выбор логики------------------------
        JPanel logic_panel=new CastomPanel();
        logic_panel.setPreferredSize(new Dimension(panel_w,150));
        infPhy_rb=new JRadioButton("Информационно-физическая модель");
        simple_rb=new JRadioButton("Модель без посредников");
        leader_rb=new JRadioButton("Модель с определенными посредниками");
        cluster_rb=new JRadioButton("Модель с динамическими посредниками");
        ButtonGroup logic_group=new ButtonGroup();
        logic_group.add(infPhy_rb);
        logic_group.add(simple_rb);
        logic_group.add(leader_rb);
        logic_group.add(cluster_rb);
        infPhy_rb.setSelected(true);
        logic_panel.add(new JLabel("Модель поведения:"));
        logic_panel.add(simple_rb);
        logic_panel.add(cluster_rb);
        logic_panel.add(leader_rb);
        logic_panel.add(infPhy_rb);
        but_panel.add(logic_panel);

        //-----------------------число агентов-------------------------------------
        agent_min_num=new EntryField();
        agent_max_num=new EntryField();
        agent_min_num.setText("40");
        agent_max_num.setText("60");
        JPanel agent_num_panel=new CastomPanel();
        agent_num_panel.setPreferredSize(new Dimension(panel_w,100));
        agent_num_panel.add(new JLabel("Минимальное количество агентов:"));
        agent_num_panel.add(agent_min_num);
        agent_num_panel.add(new JLabel("Максимальное количество агентов:"));
        agent_num_panel.add(agent_max_num);
        but_panel.add(agent_num_panel);

        //------------------диверсанты--------------------------------
        saboteur_percent_min=new EntryField();
        saboteur_percent_min.setText("5");
        message_filter_percent_min=new EntryField();
        message_filter_percent_min.setText("80");
        center_filter_percent_min=new EntryField();
        saboteur_percent_max=new EntryField();
        saboteur_percent_max.setText("30");
        message_filter_percent_max=new EntryField();
        message_filter_percent_max.setText("80");
        center_filter_percent_max=new EntryField();
        JPanel saboteur_panel=new CastomPanel();
        saboteur_panel.setPreferredSize(new Dimension(panel_w,300));
        saboteur_panel.add(new JLabel("Минимальный % диверсантов:"));
        saboteur_panel.add(saboteur_percent_min);
        saboteur_panel.add(new JLabel("Максимальный % диверсантов::"));
        saboteur_panel.add(saboteur_percent_max);
        saboteur_panel.add(new JLabel("Минимальный % фильтрации сообщений:"));
        saboteur_panel.add(message_filter_percent_min);
        saboteur_panel.add(new JLabel("Максимальный % фильтрации сообщений:"));
        saboteur_panel.add(message_filter_percent_max);
        saboteur_panel.add(new JLabel("Минимальный % фильтрации выбора центра:"));
        saboteur_panel.add(center_filter_percent_min);
        saboteur_panel.add(new JLabel("Максимальный % фильтрации выбора центра:"));
        saboteur_panel.add(center_filter_percent_max);
        but_panel.add(saboteur_panel);

        //----------------------------коэфициенты------------------------------------
        len_koef=new EntryField();
        len_koef.setText("2");
        dist_koef=new EntryField();
        analys_koef=new EntryField();
        JPanel koef_panel=new CastomPanel();
        koef_panel.setPreferredSize(new Dimension(panel_w, 170));
        koef_panel.add(new JLabel("Коэффициенты"));
        koef_panel.add(new JLabel("Коэффициент длины сообщения:"));
        koef_panel.add(len_koef);
        koef_panel.add(new JLabel("Коэффициент дистанции передачи:"));
        koef_panel.add(dist_koef);
        koef_panel.add(new JLabel("Коэффициент анализа сообщения:"));
        koef_panel.add(analys_koef);
        but_panel.add(koef_panel);

        final JScrollPane sp=new JScrollPane(but_panel);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setPreferredSize(new Dimension(panel_w+50,h));
        add(sp);

        //----------------------панель роботов---------------------------------------------------------
        robots_panel=new RobotsPanel(w-300,h);
        robots_panel.setPreferredSize(new Dimension(w-300,h));
        add(robots_panel);

        setVisible(true);
    }

    public String getLogic(){
        if (simple_rb.isSelected()){
            return "SIMPLE";
        }
        if (leader_rb.isSelected()){
            return "LEADER";
        }
        if (infPhy_rb.isSelected()){
            return "InfPhy";
        }
        if (cluster_rb.isSelected()){
            return "CLUSTER";
        }
        return null;
    }

    public int getMinAgent(){
        return Integer.parseInt(agent_min_num.getText());
    }

    public int getMaxAgent(){
        return Integer.parseInt(agent_max_num.getText());
    }

    public int getMaxSaboteurPercent(){
        return Integer.parseInt(saboteur_percent_max.getText());
    }

    public int getMinSaboteurPercent(){
        return Integer.parseInt(saboteur_percent_min.getText());
    }

    public int getMaxMessageFilterPercent(){
        return Integer.parseInt(message_filter_percent_max.getText());
    }

    public int getMinMessageFilterPercent(){
        return Integer.parseInt(message_filter_percent_min.getText());
    }

    public int getMaxCenterFilterPercent(){
        return Integer.parseInt(center_filter_percent_max.getText());
    }

    public int getMinCenterFilterPercent(){
        return Integer.parseInt(center_filter_percent_min.getText());
    }

    public int getLenKoef(){
        return Integer.parseInt(len_koef.getText());
    }

    public int getDistKoef(){
        return Integer.parseInt(dist_koef.getText());
    }

    public int getAnalyseKeefe() {
        return Integer.parseInt(analys_koef.getText());
    }

    public int getExpNum(){
        return Integer.parseInt(experiment_num.getText());
    }

    public int getSeed(){
        return Integer.parseInt(seed.getText());
    }

    public int getMinMsgLen(){
        return Integer.parseInt(min_msg_len.getText());
    }

    public int getMaxMsgLen(){
        return Integer.parseInt(max_msg_len.getText());
    }

    public boolean getDrawable(){
        return draw.isSelected();
    }

    public int getTaskNum(){
        return Integer.parseInt(task_num.getText());
    }

    public void reset(){
        lines=new ArrayList<Line>();
        progressBar.setMaximum(Params.EXPERIMENT_NUM);
        progressBar.setValue(Simulator.cur_exp);
        repaint();
    }

    public synchronized void addLine(Point p1, Point p2, Color color){
        Line l=new Line(p1, p2, color);
        if (!lines.contains(l)) {
            lines.add(l);
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



    public void setIterNum(int n){
        iterProgressBar.setValue(n);
    }

    public void setMaxIter(int n){
        iterProgressBar.setMaximum(n);
    }

    public void log(String s){
        log_string.setText(s);
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
            if (Params.DRAW.equals("false")){
                return;
            }
            g.setColor(Color.WHITE);
            g.fillRect(0,0,w,h);
            for (int i=0; i<lines.size();i++) {
                lines.get(i).draw(g);
            }
            if (Simulator.getStatus()!= Simulator.Status.WORKING){
                return;
            }
            for (int i = 0; i< Simulator.agents.size(); i++){
                Agent a= Simulator.agents.get(i);
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
            if (Simulator.taskType.getType()=="InfPhy"){
                g.setColor(Color.BLUE);
                g.drawLine(Params.divide+25+ Params.R,0, Params.divide+ Params.R+25, Params.height);
                g.drawString("INFORMATION CLUSTER",30,30);
                g.drawString("PHYSICAL CLUSTER", Params.width/2+60,30);
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
