package romanow.abc.ess2.android;

import romanow.abc.core.entity.subjectarea.NamedEntity;

public class LEP500Params extends NamedEntity {
    public String paramListName="...";
    public double FirstFreq=0.4;               // Нижняя граница частоты при поиске максимусов
    public double LastFreq=30;                 // Верхняя граница частоты при поиске максимусов
    public int  nTrendPoints=50;               // Точек при сглаживании тренда в волне =0 - отключено
    public int  nTrendPointsSpectrum=200;      // Точек при сглаживании тренда в спектре =0 - отключено
    public int  p_BlockSize=1;                 // Количество блоков по 1024 отсчета
    public int  p_OverProc=50;                 // Процент перекрытия окна
    public int  kSmooth=30;                    // Циклов сглаживания
    public int  winFun= 0;                      // Вид функции окна
    public int  measureDuration=10;            // Время снятия вибрации в сек (1...300)
    public String measureGroup="СМ-300";       // Название линии
    public String measureTitle="Опора 125";    // Название опоры
    public int measureCounter=1;               // Счетчик измерения
    public boolean fullInfo=false;
    public double measureFreq=102.8;            // Частота измерений
    public int amplLevelProc=50;                // Уровень амплитуды пика для отсечения
    public int powerLevelProc=20;               // Уровень мощности пика для отсечения
    public double alarmFreqKoef=1.25;           // относительная ширина диапазона для тревоги
    public String mailToSend="romanow@ngs.ru";
    public double K1=0.3;                       // Уровень нормированной амплитуды основого пика - слабый сигнал
    public double K2=0.8;                       // Уровень остальных к основному - отсутствие основного пика
    public double K3=0.7;                       // Уровень второстепенного пика к основному
    public double K4=0.2;                       // Относительная частота второстепенного пика
    public double K5=0.2;                       // Снижение влияния с разницей частот
    public double K6=3;                         // Уровень заленого
    public double K7=1.5;                       // Уровень желтого
    public String getTitle(){
        return paramListName;
    }

}
