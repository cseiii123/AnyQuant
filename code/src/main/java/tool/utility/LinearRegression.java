package tool.utility;

import tool.exception.BadInputException;

/**
 * 用于计算两组数据的线性回归系数
 * Created by JiachenWang on 2016/4/13.
 */
public class LinearRegression {

    public static double calculateLR_b(double[] x, double[] y, int count) throws BadInputException {
        if((x.length!=y.length)||(x.length!=count))
            throw new BadInputException("计算回归系数，数据格式出现错误");
        double result = 0;
        double xy = 0;
        double xx = 0;

        for(int i=0;i<count;i++){
            xy += x[i] * y[i];
        }

        for(int i=0;i<count;i++){
            xx += Math.pow(x[i], 2);
        }

        result = xy - 1.0 * count * calAvg(x) * calAvg(y);
        System.out.println(result + "wwww");
        result = result / (xx - count * Math.pow(calAvg(x), 2));
        return  result;
    }

    public static double calculateLR_a(double[] x, double[] y, int count) throws BadInputException {
        if((x.length!=y.length)||(x.length!=count))
            throw new BadInputException("计算回归系数，数据格式出现错误");
        return calAvg(y) - calculateLR_b(x, y, count) * calAvg(x);
    }

    public static double calAvg(double[] arg){
        double sum = 0;
        for(double tmp:arg)
            sum += tmp;

        return sum / arg.length;
    }
}
