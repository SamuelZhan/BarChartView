package com.samuelzhan.chartview;

import android.os.Bundle;

import com.samuelzhan.chartview.BarChartView.BarChartViewSettings;

import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//默认的chart
		LinearLayout chartLayout1=(LinearLayout)findViewById(R.id.chart_layout1);				
		BarChartView.BarChartViewSettings settings1=new BarChartViewSettings();
		BarChartView chart1=new BarChartView(this, settings1);			
		chartLayout1.addView(chart1);
		
		//设置后的chart
		LinearLayout chartLayout2=(LinearLayout)findViewById(R.id.chart_layout2);		
		BarChartView.BarChartViewSettings settings2=new BarChartViewSettings();		
		//设置最大值，加200，即最大值的20%左右，可以防止柱头的值超出顶端边界
		settings2.setMaxValue(1000+200);		
		//设置12个月各个月的数据
		int[] months=new int[12];
		for(int i=0; i<months.length; i++){
			months[i]=(int) (1000*Math.random());
		}
		settings2.setData(months);
		
		//设置12个月的标签
		String[] labels=new String[12];
		for(int i=0; i<labels.length; i++){
			labels[i]=Integer.toString(i+1);
		}
		settings2.setLabels(labels);
		
		//因为layout背景为蓝色，所以颜色全设为白色
		settings2.setDataLineColor(0xffffffff);
		settings2.setDataPointColor(0xffffffff);
		settings2.setDataValuesColor(0xffffffff);
		settings2.setLabelsTextColor(0xffffffff);
		
		BarChartView chart2=new BarChartView(this, settings2);	
		
		chartLayout2.addView(chart2);
		
	}

}
