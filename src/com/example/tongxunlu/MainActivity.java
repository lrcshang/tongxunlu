package com.example.tongxunlu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.nsg.util.PinYinKit;
import org.nsg.util.PinyinComparator;
import org.nsg.views.SearchEditText;
import org.nsg.views.SideBar;
import org.nsg.views.SideBar.OnTouchingLetterChangedListener;
import org.nsg.view.adapter.SortAdapter;
import org.nsg.ycontact.model.SortModel;
import org.nsg.ycontact.vo.ContatUser;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity
{
	public PinyinComparator comparator = new PinyinComparator();
	
	private ImageView groupImg;
	private ImageView backImg;
	private TextView userListNumTxt;
	private String userListNumStr;
	
	private SideBar  sideBar;
	private ListView sortListView;
	private TextView dialogTxt;
	private SearchEditText mSearchEditText;
	private SortAdapter adapter;
	
	private List<SortModel> sortModelList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.userListNumTxt = (TextView) findViewById(R.id.txt_user_list_user_num);
		
		this.backImg = (ImageView)findViewById(R.id.img_user_list_back);
		
		this.groupImg = (ImageView)findViewById(R.id.img_user_list_group);

		
		sideBar = (SideBar) findViewById(R.id.sild_bar);
		dialogTxt = (TextView) findViewById(R.id.txt_dialog);
		sideBar.setmTextDialog(dialogTxt);
		
		// on touching listener of side bar
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener()
		{
			
			public void onTouchingLetterChanged(String str)
			{
				int position =  adapter.getPositionForSection(str.charAt(0));
				if (position != -1)
					sortListView.setSelection(position);
			}
		});
		
		sortListView = (ListView) findViewById(R.id.list_view_user_list);
		sortListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		sortListView.setOnItemClickListener(new OnItemClickListener()
		{
	
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				Toast.makeText(getApplicationContext(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		// call filledData to get datas
		try
		{
			sortModelList =  filledData(getResources().getStringArray(R.array.date));
		} catch (BadHanyuPinyinOutputFormatCombination e1)
		{
			e1.printStackTrace();
		}
		
		userListNumTxt.setText("全部："+"\t"+sortModelList.size()+"个联系人");
		
		// sort by a-z
		Collections.sort(sortModelList, comparator);
		adapter = new SortAdapter(getApplicationContext(), sortModelList);
		sortListView.setAdapter(adapter);
		
		
		// search 
		mSearchEditText = (SearchEditText) findViewById(R.id.txt_filter_edit);
		
		// filter 
		mSearchEditText.addTextChangedListener(new TextWatcher()
		{
			
			public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3)
			{
				try
				{
					filerData(str.toString());
				} 
				catch (BadHanyuPinyinOutputFormatCombination e)
				{
					e.printStackTrace();
				}
			}
			
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3)
			{
			}
			
			public void afterTextChanged(Editable arg0)
			{
			}
		});
	}

	private List<SortModel> filledData(String [] date) throws BadHanyuPinyinOutputFormatCombination{
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<date.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			//汉字转换成拼音
			String pinyin = PinYinKit.getPingYin(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	private void filerData(String str) throws BadHanyuPinyinOutputFormatCombination
	{
		List<SortModel> fSortModels = new ArrayList<SortModel>();
		
		if (TextUtils.isEmpty(str))
			fSortModels = sortModelList;
		else 
		{
			fSortModels.clear();
			for (SortModel sortModel : sortModelList)
			{
				String name = sortModel.getName();
				if (name.indexOf(str.toString()) != -1 || 
						PinYinKit.getPingYin(name).startsWith(str.toString()) || PinYinKit.getPingYin(name).startsWith(str.toUpperCase().toString()))
				{
					fSortModels.add(sortModel);
				}
			}
			
		}
		Collections.sort(fSortModels, comparator);
		adapter.updateListView(fSortModels);
	}
	
	public void changeDatas(List<SortModel> mSortList , String str)
	{
		userListNumTxt.setText(str+"："+"\t"+mSortList.size()+"个联系人");
		
		Collections.sort(mSortList, comparator);
		adapter.updateListView(mSortList);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
