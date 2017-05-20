import java.io.File;
import java.io.PrintWriter;

/**
 * Created by shepkan on 27.01.2017.
 */
public class Stats {

    private double
            missing_action,
            bad_center_time,            //общее время прибывания плохих агентов центрами
            bad_center_num,             //кол-во плохих центров
            inf_agents,                 //кол-во информационных агентов
            bad_messages,               //все плохие сообщения
            wrong_phy_action,           //неверное действие phy агента для InfPhy
            iteration_num,              //кол-во итераций на эксперимент для InfPhy
            unique_center_phy,          //уникальные центры Phy для InfPhy
            unique_center_inf,          //уникальные центры Inf для InfPhy
            all_messages,               //общее кол-во соощений
            negative_message_source,    //количество первоисточников негативных сообщений включая фидбэк
            negative_message_with_intermediaries, //количество негативных сообщений посланых не на прямую
            negative_message_resended,  //посредниками негативных сообщений отправлено
            tasks,                      //колличество заданий
            steps,                      //колличество шагов агента, до отправки целевого сообщения
            time,                       //время от появления задания, до доставки сообщения
            chain_length,               //колличество агентов, через которых прошло целевое сообщение
            distance;                   //дистанция пройденная целевым сообщением
    private int broken_agents,          //сломанные агенты
                saboteur;               //диверсанты
    private int n;

    public Stats(){
        missing_action=0;
        bad_center_time=0;
        bad_center_num=0;
        inf_agents=0;
        wrong_phy_action=0;
        iteration_num=0;
        unique_center_inf=0;
        unique_center_phy=0;
        all_messages=0;
        steps=0;
        time=0;
        chain_length=0;
        distance=0;
        broken_agents=0;
        saboteur=0;
        tasks=0;
        negative_message_resended=0;
        negative_message_source=0;
        negative_message_with_intermediaries=0;
    }

    public void addWrongPhyAction(){wrong_phy_action++;}
    public void addSteps(int n){ steps=steps+n; }
    public void addTime(double d){ time=time+d;}
    public void addChain(int n){ chain_length=chain_length+n; }
    public void addDistance(double d){ distance=distance+d; }
    public void addNegativeSource(){negative_message_source++;}
    public void addNegativeWithInter(){negative_message_with_intermediaries++;}
    public void addNegativeResended(){negative_message_resended++;}

    public void setIteration_num(double iteration_num) {
        this.iteration_num = iteration_num;
    }

    public void calc(){
        n=CONST.N;
        tasks=CONST.TASK_NUM;
        for (int i=0; i<main.agents.size();i++){
            Agent a=main.agents.get(i);
            if (a.isBroken()&&!a.isSaboteur()){
                broken_agents++;
            }
            if (a.isSaboteur()){
                saboteur++;
            }
        }
        steps=steps/CONST.TASK_NUM;
        time=time/CONST.TASK_NUM;
        chain_length=chain_length/CONST.TASK_NUM+1;
        distance=distance/CONST.TASK_NUM;
        if (main.taskType.getType()=="InfPhy"){
            unique_center_inf=((InfPhyTask) (main.taskType)).getUniqueCenterNumInf();
            unique_center_phy=((InfPhyTask) (main.taskType)).getUniqueCenterNumPhy();
            for (int i=0; i<main.agents.size();i++){
                if (Clusterator.getClusters().get(0).getAgents().contains(main.agents.get(i))){
                    inf_agents++;
                }
            }
        }
        for (int i=0; i<main.agents.size();i++){
            bad_center_time+=main.agents.get(i).getBadCenterTime();
            main.agents.get(i).destory();
        }
        main.logging("AVERAGE STEPS: "+steps+"\n"+
                "AVERAGE TIME: "+time+"\n"+
                "AVERAGE CHAIN LENGTH: "+chain_length+"\n"+
                "AVERAGE DISTANCE: "+distance+"\n"+"NUM OF AGENTS: "+n+"\n"+
                "BROKEN AGENTS: "+broken_agents+"\n"+
                "SABOTEUR: "+saboteur+"\n"+
                "TASKS: "+tasks+"\n"+
                "NEGATIVE MESSAGE SOURCE: "+negative_message_source+"\n"+
                "NEGATIVE MESSAGE WITH INTERMEDIARIES: "+negative_message_with_intermediaries+"\n"+
                "NEGATIVE MESSAGE RESENDED: "+negative_message_resended+"\n"+
                "ALL MESSAGES: "+all_messages+"\n"+
                "ITEARTION NUM: "+iteration_num+"\n"+
                "UNIZUE CENTER INF: "+unique_center_inf+"\n"+
                "UNIZUE CENTER PHY: "+unique_center_phy+"\n"+
                "WRONG PHY ACTION: "+wrong_phy_action+"\n"+
                "BAD MESSAGES: "+bad_messages+"\n"+
                "INF AGNETS: "+inf_agents+"\n"+
                "BAD CENTER NUM: "+bad_center_num+"\n"+
                "BAD CENTER TIME: "+bad_center_time+"\n"+
                "MISSING ACTION: "+missing_action);
        String data[]={steps+"", time+"",chain_length+"",distance+"",n+"",broken_agents+"",saboteur+"",tasks+"",
                negative_message_source+"",negative_message_with_intermediaries+"",negative_message_resended+"",
                all_messages+"",iteration_num+"",unique_center_inf+"",unique_center_phy+"",wrong_phy_action+"",
                bad_messages+"",inf_agents+"",bad_center_num+"",bad_center_time+"",missing_action+""};
        String fileString="";
        for (String s:data){
            fileString=fileString+s+";";
        }
        fileString+="\n";
        System.out.print(fileString);
        File file=main.logFile;

        try {
            if (main.out==null) {
                main.out = new PrintWriter(file.getAbsoluteFile());
            }
            main.out.print(fileString);
            if (main.cur_exp==CONST.EXPERIMENT_NUM){
                main.out.close();
            }
        } catch(Exception e) {
            System.out.println("File error: "+e.toString());
        }
        Table.addData(data);
    }
    public void addAllMessages(){
        all_messages++;
    }
    public void addBadMessages(){bad_messages++;}
    public void addBadCenter(){bad_center_num++;}
    public void addMissingAction(){missing_action++;}
}
