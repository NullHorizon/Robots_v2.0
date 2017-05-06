/**
 * Created by shepkan on 30.01.2017.
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by shepkan on 08.07.2016.
 */
public class Table extends JFrame {
    private static boolean vi=false;
    private static JTable table=new JTable();
    private static JFrame frame=new Table();
    private static String columnNames[]={
            "steps",
            "time",
            "chain length",
            "distance",
            "num of agents",
            "broken agents",
            "saboteur",
            "tasks",
            "neg msg source",
            "neg msg with inter",
            "neg msg resended"};
    private static ArrayList<String[]> data = new ArrayList<String[]>();

    public static void addData(String[] s){
        data.add(s);
    }

    public static void showTable(){
        String[][] a=data.toArray(new String[0][0]);
        table= new JTable(a, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame = new Table();
        frame.getContentPane().add(scrollPane);
        //frame.setPreferredSize(new Dimension(900,200));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
