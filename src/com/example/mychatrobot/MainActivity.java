package com.example.mychatrobot;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mychatrobot.JsonBean.WS;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MainActivity extends Activity {
	
	private StringBuffer mysb=new StringBuffer();
	
	private ArrayList<TalkBean> talkbeanlist=new ArrayList<TalkBean>();
	
	private ListView lvlist;
	
	private int[] pics=new int[]{
		R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4
	};

	private String[] answers=new String[]{
			"我们来上第一课吧","我们来上第二课吧","我们来上第三课吧"
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 将“12345678”替换成您申请的 APPID，申请地址：http://www.xfyun.cn
		// 请勿在“=”与 appid 之间添加任务空字符或者转义符
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"=571aebc0");
		
		
		lvlist=(ListView) findViewById(R.id.lvlist);
	}
	
	
	public void listen(View v){
		//1.创建RecognizerDialog对象
		RecognizerDialog mDialog = new RecognizerDialog(this, null);
		//2.设置accent、language等参数
		mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
		//若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
		//结果
		// mDialog.setParameter("asr_sch", "1");
		// mDialog.setParameter("nlp_version", "2.0");
		//3.设置回调接口
		mDialog.setListener(new RecognizerDialogListener() {
			
			@Override
			public void onResult(RecognizerResult results, boolean islast) {
				
				String resultString=results.getResultString();
				
				System.out.println("听写的数据"+resultString);
				
				System.out.println("是否结束"+islast);
				
				String parseData = parseData(resultString);
				
				mysb.append(parseData);
				
				if(islast){
					String askcontent=mysb.toString();
					//初始化提问内容
					
					TalkBean askbean=new TalkBean(askcontent, "", true, -1);
					
					System.out.println(askcontent+"++++++++++++++++++++++++++++++");
					
					talkbeanlist.add(askbean);
					
					//初始化回答信息
					
					String answercontent="没听清";
					int imageid=-1;
					
					if(askcontent.contains("你好")){
						answercontent="你好呀！";
					}else if(askcontent.contains("你是谁")){
						answercontent="我是你的小助手啊！";
					}else if(askcontent.contains("美女")){
						answercontent=answers[(int) (Math.random()*answers.length)];
						imageid=pics[(int) (Math.random()*pics.length)];
						
					}else if(askcontent.contains("天王盖地虎")){
						answercontent="小鸡炖蘑菇";
						imageid=R.drawable.m;
					}
					
					speak(answercontent);
					
					TalkBean answerbBean=new TalkBean("", answercontent, false, imageid);
					
					talkbeanlist.add(answerbBean);
					
					MyAdapter adapter=new MyAdapter();
					
					lvlist.setAdapter(adapter);
					
					adapter.notifyDataSetChanged();
					
					lvlist.setSelection(talkbeanlist.size()-1);
					
				}
				
				
				
				
			}
			
			@Override
			public void onError(SpeechError arg0) {
				
			}
		});
		//4.显示dialog，接收语音输入
		mDialog.show();

		
	}
	
	public void speak(String answercontent){
		//1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
		SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(this, null);
		//2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
		//设置发音人（更多在线发音人，用户可参见 附录12.2
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
		//设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
		//保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
		//仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
		//mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
		//3.开始合成
		mTts.startSpeaking(answercontent, null);

	}
	protected String parseData(String resultString) {
		
		StringBuffer sb=new StringBuffer();
		
		Gson gson=new Gson();
		JsonBean jsonBean = gson.fromJson(resultString, JsonBean.class);
		
		ArrayList<WS> ws = jsonBean.ws;
		
		for (WS w : ws) {
			String string = w.cw.get(0).w;
			
			sb.append(string);
		}
		
		return sb.toString();
	}
	

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return talkbeanlist.size();
		}

		@Override
		public TalkBean getItem(int position) {
			// TODO Auto-generated method stub
			return talkbeanlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			ViewHodler viewhodler;
			
			TalkBean item=getItem(position);
			
			if(convertView==null){
				
				convertView=View.inflate(getApplicationContext(), R.layout.list_item, null);
				
				viewhodler=new ViewHodler();
				viewhodler.tvask=(TextView) convertView.findViewById(R.id.tv_ask);
				viewhodler.tvanswer=(TextView) convertView.findViewById(R.id.tv_answer);
				viewhodler.llanswer=(LinearLayout) convertView.findViewById(R.id.ll_answer);
				viewhodler.iv_pic=(ImageView) convertView.findViewById(R.id.iv_pic);
				
				convertView.setTag(viewhodler);
						
			}else{
				
				viewhodler = (ViewHodler)convertView.getTag();
			}
			if(item.isask){
				//表示是提问
				viewhodler.tvask.setVisibility(View.VISIBLE);
				viewhodler.llanswer.setVisibility(View.GONE);
				
				viewhodler.tvask.setText(item.askcontent);
				
				
			}else{
				//表示是回答
				
				viewhodler.tvask.setVisibility(View.GONE);
				viewhodler.llanswer.setVisibility(View.VISIBLE);
				
				viewhodler.tvanswer.setText(item.answercontent);
				
				if(item.imageId>0){
					viewhodler.iv_pic.setVisibility(View.VISIBLE);
					viewhodler.iv_pic.setImageResource(item.imageId);
				}else{
					viewhodler.iv_pic.setVisibility(View.GONE);
				}
			}
			
			
			
			return convertView;
		}
		
		class ViewHodler{
			public TextView tvask;
			public TextView tvanswer;
			public LinearLayout llanswer;
			public ImageView iv_pic;
		}
	}
	
}
