package com.samuelzhan.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class BarChartView extends View {
	
	//数据
	private int[] data;
	//x轴标签，最好应该和数据长度一致
	private String[] labels;
	//left，top，right，bottom的padding数组
	private int[] paddings;
	//柱条的间隔
	private int interval;
	//被选中的数据，即touch事件后对应的柱子
	private int selectedData;
	//是否显示柱子被选中后添加背景色
	private boolean showSelectedShade;
	//标签文本大小
	private int labelsTextSize;
	//标签文本颜色
	private int labelsTextColor;
	//是否显示数据竖虚线
	private boolean showDashLine;
	//竖虚线的颜色
	private int dataLineColor;
	//竖虚线的宽
	private int dataLineWidth;
	//竖虚线顶部的圆点边宽
	private int dataPointStrokeWidth;
	//竖虚线顶部的圆点半径
	private int dataPointRadius;
	//竖虚线顶部的圆点颜色
	private int dataPointColor;
	//圆点是否填充
	private boolean fillPoint;
	//是否显示值
	private boolean showDataValues;
	//值的颜色
	private int dataValuesColor;
	//值的大小
	private int dataValuesSize;
	//最大值
	private int maxValue;
	private Paint paint;

	
	public BarChartView(Context context, BarChartView.BarChartViewSettings settings){
		super(context);

		maxValue=settings.getMaxValue();				
		data=settings.getData();		
		labels=settings.getLabels();		
		paddings=settings.getPaddings();
		labelsTextSize=settings.getLabelsTextSizeInSp();
		labelsTextColor=settings.getLabelsTextColor();
		showDashLine=settings.isShowDashLine();
		dataLineColor=settings.getDataLineColor();
		dataLineWidth=settings.getDataLineWidthInDp();
		dataPointStrokeWidth=settings.getDataPointStrokeWidthInDp();
		dataPointRadius=settings.getDataPointRadiusInDp();
		dataPointColor=settings.getDataPointColor();
		fillPoint=settings.isFillPoint();
		showDataValues=settings.isShowDataValues();
		dataValuesColor=settings.getDataValuesColor();
		dataValuesSize=settings.getDataValuesSizeInSp();
		selectedData=settings.getSelectedData();
		showSelectedShade=settings.isShowSelectedShade();
		paint=new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub		
		
		//数据间的间隔
		interval=(getWidth()-paddings[0]-paddings[2])/data.length;		
	
		//画X轴下的labels
		paint.setColor(labelsTextColor);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(sp2px(labelsTextSize));
		FontMetrics fm=paint.getFontMetrics();
		float textHeight=fm.bottom-fm.top;
		for(int i=0; i<labels.length; i++){
			//减去baseline到bottom的高度，大约为文字高度的1/5
			canvas.drawText(labels[i], interval*i+interval/2+paddings[0], getHeight()-textHeight/5-paddings[3], paint);
		}
		
		//画X轴
		paint.setStrokeWidth(2f);
		paint.setStyle(Style.STROKE);
		Path path=new Path();
		path.moveTo(0+paddings[0], getHeight()-textHeight-paddings[3]);
		path.lineTo(getWidth()-paddings[2], getHeight()-textHeight-paddings[3]);
		canvas.drawPath(path, paint);
		
		//画每个数据位置对应的竖虚线，即纵网格线
		if(showDashLine){
			paint.setStrokeWidth(dp2px(dataLineWidth));
			paint.setColor(dataLineColor);
			//虚线效果
			DashPathEffect effect=new DashPathEffect(new float[]{10, 10}, 0);
			paint.setPathEffect(effect);
			for(int i=0; i<data.length; i++){
				path.reset();
				path.moveTo(interval*i+interval/2+paddings[0], getHeight()-textHeight-paddings[3]);
				path.lineTo(interval*i+interval/2+paddings[0], 0+paddings[1]);
				canvas.drawPath(path, paint);
			}
		}
		
		//画数据的实线和数据的圆点
		paint.setColor(dataPointColor);
		paint.setStrokeWidth(dp2px(dataPointStrokeWidth));
		if(fillPoint){
			paint.setStyle(Style.FILL_AND_STROKE);
		}else{
			paint.setStyle(Style.STROKE);
		}		
		//除去刚才画虚线的效果
		paint.setPathEffect(null);	
		for(int i=0; i<data.length; i++){
			path.reset();
			path.moveTo(interval*i+interval/2+paddings[0], getHeight()-textHeight-paddings[3]);
			path.lineTo(interval*i+interval/2+paddings[0], 
					(getHeight()-textHeight-paddings[3]-paddings[1])*(1-(float)data[i]/maxValue)+paddings[1]+dp2px(dataPointRadius));
			canvas.drawPath(path, paint);			
			canvas.drawCircle(interval*i+interval/2+paddings[0], 
					(getHeight()-textHeight-paddings[3]-paddings[1])*(1-(float)data[i]/maxValue)+paddings[1],
					dp2px(dataPointRadius), paint);						
		}
		
		//画值在点上面
		if(showDataValues){
			paint.setTextSize(sp2px(dataValuesSize));
			paint.setColor(dataValuesColor);
			paint.setStyle(Style.FILL);
			for(int i=0; i<data.length; i++){
				canvas.drawText(Integer.toString(data[i]), interval*i+interval/2+paddings[0],
						(getHeight()-textHeight-paddings[3]-paddings[1])*(1-(float)data[i]/maxValue)+paddings[1]-dp2px(dataPointRadius+2), paint);
			}	
		}
		
		//如果有显示点击效果，则在该bar的背景用Rect画一个背景色即可
		if(showSelectedShade){
			Rect rect=new Rect(paddings[0]+interval*selectedData, paddings[1], 
					paddings[0]+interval*(selectedData+1),
					(int) (getHeight()-paddings[3]-textHeight));
			paint.setColor(0x33000000);
			paint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawRect(rect, paint);
			paint.reset();
		}
		
		
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			if(showSelectedShade){
				if(event.getX()>getWidth()-paddings[2]||event.getX()<paddings[0]){
					return false;
				}
				selectedData=(int)(event.getX()-paddings[0])/interval;
				invalidate();
				
			}
		}		
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private int sp2px(int sp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
	}
	
	private int dp2px(int dp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
	
	//参数类,用于初始化BarChartView
	public static class BarChartViewSettings{
		
		//数据
		private int[] data;
		//x轴标签，最好应该和数据长度一致
		private String[] labels;
		//left，top，right，bottom的padding数组
		private int[] paddings;
		//标签文本大小
		private int labelsTextSizeInSp;
		//标签文本颜色
		private int labelsTextColor;
		//是否显示数据竖虚线，即纵网格
		private boolean showDashLine;
		//竖虚线的颜色
		private int dataLineColor;
		//竖虚线的宽
		private int dataLineWidthInDp;
		//竖虚线顶部的圆点边宽
		private int dataPointStrokeWidthInDp;
		//竖虚线顶部的圆点半径
		private int dataPointRadiusInDp;
		//竖虚线顶部的圆点颜色
		private int dataPointColor;
		//圆点是否填充
		private boolean fillPoint;
		//是否在柱头显示值
		private boolean showDataValues;
		//值的颜色
		private int dataValuesColor;
		//值的大小
		private int dataValuesSizeInSp;
		//被选中的数据，即touch事件后对应的柱子序号
		private int selectedData;
		//是否显示柱子被选中后添加背景色
		private boolean showSelectedShade;
		//最大值
		private int maxValue;

		public BarChartViewSettings(){
			
			//表格最大值
			maxValue=100;
			
			//没有数据则默认为长度为7的随机数组
			data=new int[7];
			for(int i=0; i<data.length; i++){
				data[i]=(int) (Math.random()*maxValue);
			}
			
			//没有标签则默认为长度为7的星期一至星期日
			labels=new String[]{"一", "二", "三", "四", "五", "六", "日"};
			
			//默认没有paddings
			paddings=new int[]{0, 0, 0, 0};
			
			//以下均为初始化的默认值
						
			labelsTextSizeInSp=16;
			labelsTextColor=Color.BLACK;
			showDashLine=true;
			dataLineColor=Color.BLACK;
			dataLineWidthInDp=1;
			dataPointStrokeWidthInDp=2;
			dataPointRadiusInDp=4;
			dataPointColor=Color.BLACK;
			fillPoint=false;
			showDataValues=true;
			dataValuesColor=Color.BLACK;
			dataValuesSizeInSp=12;
			showSelectedShade=true;
			selectedData=0;
			
		}	

		public int[] getPaddings() {
			return paddings;
		}

		public void setPaddings(int left, int top, int right, int bottom) {
			paddings=new int[4];
			paddings[0]=left;
			paddings[1]=top;
			paddings[2]=right;
			paddings[3]=bottom;
		}

		public int getSelectedData() {
			return selectedData;
		}

		public void setSelectedData(int selectedData) {
			this.selectedData = selectedData;
		}


		public boolean isShowSelectedShade() {
			return showSelectedShade;
		}


		public void setShowSelectedShade(boolean showSelectedShade) {
			this.showSelectedShade = showSelectedShade;
		}


		public int getLabelsTextSizeInSp() {
			return labelsTextSizeInSp;
		}


		public void setLabelsTextSizeInSp(int labelsTextSizeInSp) {
			this.labelsTextSizeInSp = labelsTextSizeInSp;
		}


		public int getDataLineWidthInDp() {
			return dataLineWidthInDp;
		}

		public void setDataLineWidthInDp(int dataLineWidthInDp) {
			this.dataLineWidthInDp = dataLineWidthInDp;
		}



		public int getDataPointStrokeWidthInDp() {
			return dataPointStrokeWidthInDp;
		}


		public void setDataPointStrokeWidthInDp(int dataPointStrokeWidthInDp) {
			this.dataPointStrokeWidthInDp = dataPointStrokeWidthInDp;
		}


		public int getDataPointRadiusInDp() {
			return dataPointRadiusInDp;
		}


		public void setDataPointRadiusInDp(int dataPointRadiusInDp) {
			this.dataPointRadiusInDp = dataPointRadiusInDp;
		}


		public int getDataValuesSizeInSp() {
			return dataValuesSizeInSp;
		}

		public void setDataValuesSizeInSp(int dataValuesSizeInSp) {
			this.dataValuesSizeInSp = dataValuesSizeInSp;
		}

		public int getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(int maxValue) {
			this.maxValue = maxValue;
		}

		public int[] getData() {
			return data;
		}
		public void setData(int[] data) {
			this.data = data;
		}
		public String[] getLabels() {
			return labels;
		}
		public void setLabels(String[] labels) {
			this.labels = labels;
		}
		public int getLabelsTextColor() {
			return labelsTextColor;
		}
		public void setLabelsTextColor(int labelsTextColor) {
			this.labelsTextColor = labelsTextColor;
		}
		public boolean isShowDashLine() {
			return showDashLine;
		}
		public void setShowDashLine(boolean showDashLine) {
			this.showDashLine = showDashLine;
		}
		public int getDataLineColor() {
			return dataLineColor;
		}
		public void setDataLineColor(int dataLineColor) {
			this.dataLineColor = dataLineColor;
		}
		public int getDataPointColor() {
			return dataPointColor;
		}
		public void setDataPointColor(int dataPointColor) {
			this.dataPointColor = dataPointColor;
		}
		public boolean isFillPoint() {
			return fillPoint;
		}
		public void setFillPoint(boolean fillPoint) {
			this.fillPoint = fillPoint;
		}
		public boolean isShowDataValues() {
			return showDataValues;
		}
		public void setShowDataValues(boolean showDataValues) {
			this.showDataValues = showDataValues;
		}
		public int getDataValuesColor() {
			return dataValuesColor;
		}
		public void setDataValuesColor(int dataValuesColor) {
			this.dataValuesColor = dataValuesColor;
		}

		
	}

}
