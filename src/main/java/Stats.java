/**
 * Created by shepkan on 27.01.2017.
 */
public class Stats {

    private double
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

    public void addSteps(int n){ steps=steps+n; }
    public void addTime(double d){ time=time+d;}
    public void addChain(int n){ chain_length=chain_length+n; }
    public void addDistance(double d){ distance=distance+d; }
    public void addNegativeSource(){negative_message_source++;}
    public void addNegativeWithInter(){negative_message_with_intermediaries++;}
    public void addNegativeResended(){negative_message_resended++;}

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
                "ALL MESSAGES: "+all_messages);
        String data[]={steps+"", time+"",chain_length+"",distance+"",n+"",broken_agents+"",saboteur+"",tasks+"",
                negative_message_source+"",negative_message_with_intermediaries+"",negative_message_resended+"",
                all_messages+""};
        Table.addData(data);
        main.next();
    }
    public void addAllMessages(){
        all_messages++;
    }
}
