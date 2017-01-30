import java.awt.*;
import java.awt.event.*;

/**
 * Created by Yura on 30.01.2017.
 */
// Програма для отображения графика функции.
public class PlotFrame extends Frame
{
    // конструктор (аргументы - высота и ширина окна):
    PlotFrame(int H, int W)
    {
        // Заголовок окна:
        setTitle("График функции");
        // положение и размер окна
        setBounds(100,50,W,H);
        // цвет фона окна
        setBackground(Color.GRAY);
        // отключение менеджера размещения элементов
        setLayout(null);
        // Определение шрифта
        Font f = new Font("Arial",Font.BOLD,11);
        // применение шрифта
        setFont(f);
        // Создание панели с кнопками
        BPanel BPnl = new BPanel(6,25,W/4,H-30);
        // добавление панели в главное окно
        add(BPnl);
        // панель для отображени графика (создание):
        PPanel PPnl = new PPanel(W/4+10,25,3*W/4-15,H-120,BPnl);
        //добавление панели в главное окно:
        add(PPnl);
        // третья панель для отображения справки:
        HPanel HPnl = new HPanel(W/4+10,H-90,3*W/4-15,85);
        // Добавление панели в главное окно:
        add(HPnl);
        // Регистрация обработчика в окне (закрытие окна):
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Закрытие окна
            }
        });
        // Регистрация обработчика для первой кнопки:
        BPnl.B1.addActionListener(new Button1Pressed(BPnl,PPnl));
        // Регистрация обработчика для второй кнопки:
        BPnl.B2.addActionListener(new Button2Pressed());
        // регистрация обработчика для флажка вывода сетки:
        BPnl.Cb[3].addItemListener(new cbChanged(BPnl));
        // размер окна (фрейма) не изменяется:
        setResizable(false);
        // значок для окна программы:
        setIconImage(getToolkit().getImage("D:\\Особисте\\PioClementinum.png"));
        // Отображение окна
        setVisible(true);
    }
    // Класс панели с кнопками:
    class BPanel extends Panel
    {
        // метки панели:
        public Label [] L;
        // Группа переключателей панели:
        public CheckboxGroup CbG;
        // переключатели панели:
        public Checkbox[] Cb;
        // Раскрывающийся список:
        public Choice Ch;
        // Текстовое поле:
        public TextField TF;
        // Кнопки панели:
        public Button B1,B2;
        // конструктор аргументы - координаты и размеры панели
        BPanel(int x,int y,int W,int H)
        {
            // Отключение менеджера размещения элементов на панели:
            setLayout(null);
            // положение и размер панели
            setBounds(x,y,W,H);
            // цвет фона панели
            setBackground(Color.LIGHT_GRAY);
            // массив меток:
            L=new Label[3];
            // текстовая метка:
            L[0] = new Label("Выбор цвета:",Label.CENTER);
            // Шрифт для текстовой метки:
            L[0].setFont(new Font("Arial",Font.BOLD,12));
            // размеры метки:
            L[0].setBounds(5,5,getWidth()-10,30);
            // Добавление метки на панель:
            add(L[0]);
            // Группа переключателей:
            CbG=new CheckboxGroup();
            Cb = new Checkbox[4];
            // переключатели группы:
            Cb[0] = new Checkbox("красный",CbG,true);
            Cb[1] = new Checkbox("синий",CbG,false);
            Cb[2] = new Checkbox("черный",CbG,false);
            // флажок вывода сетки:
            Cb[3]=new Checkbox("сетка",true);
            // размеры переключателей и флажка и добавление их на панель
            for(int i=0;i<4;i++)
            {
                Cb[i].setBounds(5,30+i*25,getWidth()-10,30); // размер
                add(Cb[i]);
            }
            // раскрывающийся список выбора цвета для линий сетки:
            Ch=new Choice();
            // добавление элемента "Зеленый"
            Ch.add("Зеленый");
            // добавление элемента "Желтый"
            Ch.add("Желтый");
            // добавление элемента "Серый"
            Ch.add("Серый");
            // размер и положение раскрывающегося списка:
            Ch.setBounds(20,140,getWidth()-25,30);
            // добавление списка на панель
            add(Ch);
            // вторая тестовая метка:
            L[1]=new Label("Интервал по оси х:", Label.CENTER);
            // шрифт для метки:
            L[1].setFont(new Font("Arial",Font.BOLD,12));
            // размер и положение метки:
            L[1].setBounds(5,220,getWidth()-10,30);
            // добавление метки на панель:
            add(L[1]);
            // третья текстовая метка:
            L[2]=new Label("От х=0 до х=",Label.LEFT);
            // размер и положение метки
            L[2].setBounds(5,250,70,20);
            // добавление метки на панель:
            add(L[2]);
            // текстовое поле для ввода границы интервала:
            TF=new TextField("10");
            // размер и положение поля
            TF.setBounds(75,250,45,20);
            // добавление поля на панель:
            add(TF);
            // первая кнопка
            B1=new Button("Нарисовать");
            // вторая кнопка
            B2=new Button("Закрыть");
            // размеры и положение первой кнопки:
            B1.setBounds(5,getHeight()-75,getWidth()-10,30);
            // размер и положение второй кнопки:
            B2.setBounds(5,getHeight()-35,getWidth()-10,30);
            add(B1); // добавление на панель
            add(B2);
        }
    }
    // класс панели для отображения графика:
    class PPanel extends Panel
    {
        // ссылка на обьект реализации графика функции:
        public Plotter G;
        // внутренный класс для реализации графика функции:
        class Plotter
        {
            // границы диапазона изменения координат:
            private double Xmin=0,Xmax,Ymin=0,Ymax=1.0;
            // состояние флажка вывода сетки:
            private boolean status;
            // цвет для линии графика:
            private Color clr;
            // Цвет для отображения линий сетки:
            private Color gclr;
            // конструктор класса аргументы - панель с кнопками и панель для отображения графика:
            Plotter(BPanel P)
            {
                // считываем значения текстового поля и преобразуем в число:
                try{Xmax= Double.valueOf(P.TF.getText());
                }catch (NumberFormatException e)
                {
                    P.TF.setText("10");
                    Xmax=10;
                }
                status=P.Cb[3].getState();
                // определение цвета линии сетки:
                switch (P.Ch.getSelectedIndex())
                {
                    case 0:
                        gclr=Color.GREEN;
                        break;
                    case 1:
                        gclr=Color.YELLOW;
                        break;
                    default:
                        gclr=Color.GRAY;
                }
                // цвет линии графика:
                String name = P.CbG.getSelectedCheckbox().getLabel();
                if(name.equalsIgnoreCase("красный")) clr=Color.RED;
                else if(name.equalsIgnoreCase("синий")) clr=Color.BLUE;
                else clr=Color.BLACK;
            }
            // отображаемая на графике функция:
            private double f(double x)
            {
                return (1+Math.sin(x))/(1+Math.abs(x));
            }
            // метод для считывания и запоминания настроек:
            public Plotter remember(BPanel P)
            {
                return new Plotter(P);
            }
            // Метод для отображения графика и сетки Fig - обьект графического контекста:
            public void plot(Graphics Fig)
            {
                // параметры области отображения графика:
                int H,W,h,w,s=20;
                H=getHeight();
                W=getWidth();
                h=H-2*s;
                w=W-2*s;
                // очистка области графика:
                Fig.clearRect(0,0,W,H);
                // индексная переменная и количество линий сетки
                int k,nums=10;
                // цвет координатных осей - черный:
                Fig.setColor(Color.BLACK);
                // отображение координатных осей:
                Fig.drawLine(s,s,s,h+s);
                Fig.drawLine(s,s+h,s+w,h+s);
                // отображение засечек и числовых значений на координатных осях:
                for(k=0;k<=nums;k++)
                {
                    Fig.drawLine(s+k*w/nums,s+h,s+k*w/nums,s+h+5);
                    Fig.drawLine(s-5,s+k*h/nums,s,s+k*h/nums);
                    Fig.drawString(Double.toString(Xmin+k*(Xmax-Xmin)/nums),s+k*w/nums-5,s+h+15);
                    Fig.drawString(Double.toString(Ymin+k*(Ymax-Ymin)/nums),s-17,s+h-1-k*h/nums);
                }
                // отображение сетки если установлен флажок
                if(status)
                {
                    Fig.setColor(gclr);
                    // отображение линий сетки:
                    for(k=1;k<=nums;k++)
                    {
                        Fig.drawLine(s+k*w/nums,s,s+k*w/nums,h+s);
                        Fig.drawLine(s,s+(k-1)*h/nums,s+w,s+(k-1)*h/nums);
                    }
                }
                // отображение графика
                Fig.setColor(clr); // установка цвета линии
                // масштаб на один пиксел по каждой из координат:
                double dx= (Xmax-Xmin)/w,dy=(Ymax-Ymin)/h;
                // переменные для записи декартовых координат:
                double x1,x2,y1,y2;
                // переменные для записи координат в окне отображения графика:
                int h1,h2,w1,w2;
                // начальные значения:
                x1=Xmin;
                y1=f(x1);
                w1=s;
                h1=h+s-(int)Math.round(y1/dy);
                // шаг в пикселах для базовых точек:
                int step=5;
                // отображение базовых точек и соединение их линиями:
                for(int i=step;i<=w;i+=step)
                {
                    x2=i*dx;
                    y2=f(x2);
                    w2=s+(int)Math.round(x2/dx);
                    h2=h+s-(int)Math.round(y2/dy);
                    // линия
                    Fig.drawLine(w1,h1,w2,h2);
                    // базовая точка (квадрат):
                    Fig.drawRect(w1-2,h1-2,4,4);
                    // новые значения для координат:
                    x1=x2;
                    y1=y2;
                    w1=w2;
                    h1=h2;
                }
            }
        }
        // конструктор панели аргументы - координаты и размеры панели
        // а также ссылка на панель с кнопками:
        PPanel(int x,int y,int W,int H,BPanel P)
        {
            // создание обьекта реализации графика функции:
            G=new Plotter(P);
            // цвет фона панели:
            setBackground(Color.WHITE);
            // размер и положение панели:
            setBounds(x,y,W,H);
        }
        // переопределение метода перерисовки панели:
        public void paint(Graphics g)
        {
            // при перерисовке панели вызывается метод для отображения графика:
            G.plot(g);
        }
    }
    // класс для панели справки:
    class HPanel extends Panel
    {
        public Label L; // метка
        // текстовая область:
        public TextArea TA;
        // конструктор создания панели
        HPanel(int x, int y,int W,int H)
        {
            // цвет фона панели:
            setBackground(Color.LIGHT_GRAY);
            // размер и положение панели:
            setBounds(x,y,W,H);
            // отключение менеджера размещения компонентов панели:
            setLayout(null);
            // метка для панели справки:
            L=new Label("СПРАВКА",Label.CENTER);
            // размер и положение метки:
            L.setBounds(0,0,W,20);
            // Добавление метки на панель:
            add(L);
            // текстовая область для панели справки:
            TA=new TextArea("График функции y(x)=(1+sin(x))/(1+|x|)");
            // шрифт для текстовой области:
            TA.setFont(new Font("Serif",Font.PLAIN,15));
            // размер и положение текстовой области:
            TA.setBounds(5,20,W-10,60);
            // область недоступна для редактирования:
            TA.setEditable(false);
            // добавление текстовой области на панель справки:
            add(TA);
        }
    }
    // коласс обработчика для первой кнопки:
    class Button1Pressed implements ActionListener
    {
        // панель с кнопками
        private BPanel P1;
        // панель для отображения графики:
        private PPanel P2;
        // конструктор класса аргументы - панели
        Button1Pressed(BPanel P1,PPanel P2)
        {
            this.P1=P1;
            this.P2=P2;
        }
        // метод для обработки щелчка на кнопке:
        public void actionPerformed(ActionEvent ae)
        {
            // обновление параметров для отображения графика:
            P2.G=P2.G.remember(P1);
            // реакция на щелчок (прорисовка графика):
            P2.G.plot(P2.getGraphics());
        }
    }
    // класс обработчик для второй кнопки:
    class Button2Pressed implements ActionListener
    {
        // метод для обработки щелчка на кнопке:
        public void actionPerformed(ActionEvent ae)
        {
            System.exit(0); // реакция на щелчок:
        }
    }
    // класс обработчика для флажка вывода сетки:
    class cbChanged implements ItemListener
    {
        // список выбора для сетки:
        private Choice ch;
        // конструктор класса аргумент - панель с кнопками:
        cbChanged(BPanel P) {this.ch=P.Ch;}
        // метод для обработки изменения состояния флажка:
        public void itemStateChanged(ItemEvent ie)
        {
            // реакция на изменение состояния флажка:
            ch.setEnabled(ie.getStateChange()==ie.SELECTED);
        }
    }
}
// класс с главным методом програмы:
class PlotDemo
{
    public static void main(String [] args)
    {
        new PlotFrame(400,500); // создание окна
    }
}
