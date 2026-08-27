package com.moskao4.musicdownloader;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.*;

public class MainActivity extends Activity {
    LinearLayout root, content, appBar;
    EditText input;
    TextView title, status, progressText;
    ProgressBar progress;
    SharedPreferences prefs;
    int themeMode;
    final int DARK_BG=Color.rgb(16,16,18), DARK_CARD=Color.rgb(28,28,31), LIGHT_BG=Color.rgb(247,247,249), LIGHT_CARD=Color.WHITE;
    final int ACCENT=Color.rgb(220,178,42);

    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    int effectiveTheme(){
        if(themeMode==0){int ui=getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK; return ui==android.content.res.Configuration.UI_MODE_NIGHT_YES?2:1;}
        return themeMode;
    }
    int bg(){return effectiveTheme()==1?LIGHT_BG:DARK_BG;}
    int card(){return effectiveTheme()==1?LIGHT_CARD:DARK_CARD;}
    int fg(){return effectiveTheme()==1?Color.rgb(28,28,30):Color.WHITE;}
    int muted(){return effectiveTheme()==1?Color.rgb(105,105,112):Color.rgb(165,165,170);}

    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("settings",0); themeMode=prefs.getInt("theme",2); buildNetease();}
    @Override public void onConfigurationChanged(android.content.res.Configuration c){super.onConfigurationChanged(c); buildNetease();}

    GradientDrawable bgDrawable(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
    TextView text(String s,float size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg());t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setTextColor(fg());b.setAllCaps(false);b.setMinHeight(0);b.setPadding(dp(12),0,dp(12),0);b.setBackground(bgDrawable(effectiveTheme()==1?Color.rgb(232,232,236):Color.rgb(39,39,43),14));return b;}
    EditText editor(String hint,boolean multi){EditText e=new EditText(this);e.setHint(hint);e.setHintTextColor(muted());e.setTextColor(fg());e.setTextSize(16);e.setPadding(dp(16),dp(12),dp(16),dp(12));e.setSingleLine(!multi);e.setGravity(multi?Gravity.TOP|Gravity.START:Gravity.CENTER_VERTICAL);e.setBackground(bgDrawable(card(),14));return e;}

    void base(String pageTitle){
        getWindow().setStatusBarColor(bg());getWindow().setNavigationBarColor(bg());
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(bg());
        appBar=new LinearLayout(this);appBar.setOrientation(LinearLayout.HORIZONTAL);appBar.setGravity(Gravity.CENTER_VERTICAL);appBar.setPadding(dp(10),dp(8),dp(10),dp(8));
        TextView menu=text("☰",26);menu.setGravity(Gravity.CENTER);menu.setOnClickListener(v->showDrawer());appBar.addView(menu,new LinearLayout.LayoutParams(dp(48),dp(52)));
        LinearLayout brand=new LinearLayout(this);brand.setGravity(Gravity.CENTER_VERTICAL);ImageView logo=new ImageView(this);logo.setImageResource(getIcon(prefs.getInt("icon",6)));logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);brand.addView(logo,new LinearLayout.LayoutParams(dp(34),dp(34)));title=text(pageTitle,19);title.setGravity(Gravity.CENTER_VERTICAL);title.setTypeface(null,android.graphics.Typeface.BOLD);brand.addView(title,new LinearLayout.LayoutParams(0,dp(52),1));appBar.addView(brand,new LinearLayout.LayoutParams(0,dp(52),1));
        TextView more=text("⋮",28);more.setGravity(Gravity.CENTER);more.setOnClickListener(v->showAbout());appBar.addView(more,new LinearLayout.LayoutParams(dp(48),dp(52)));root.addView(appBar);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(20),dp(18),dp(20),dp(28));scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    }
    void buildNetease(){base("网易云下载");
        TextView sub=text("网易云音乐",13);sub.setTextColor(muted());content.addView(sub,new LinearLayout.LayoutParams(-1,dp(30)));
        input=editor("粘贴网易云分享文本或链接",true);content.addView(input,new LinearLayout.LayoutParams(-1,dp(118)));
        Button go=button("开始下载");LinearLayout.LayoutParams gp=new LinearLayout.LayoutParams(-1,dp(50));gp.topMargin=dp(14);content.addView(go,gp);go.setOnClickListener(v->startNetease());
        progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);progress.setVisibility(View.GONE);LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(6));pp.topMargin=dp(20);content.addView(progress,pp);
        progressText=text("",13);progressText.setTextColor(muted());content.addView(progressText,new LinearLayout.LayoutParams(-1,dp(38)));
        status=text("● 等待输入",14);status.setTextColor(muted());content.addView(status,new LinearLayout.LayoutParams(-1,dp(42)));
    }
    void showNormal(){base("普通下载");input=editor("粘贴 MP3 / 音频 URL",false);content.addView(input,new LinearLayout.LayoutParams(-1,dp(58)));Button b=button("开始下载");LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.topMargin=dp(14);content.addView(b,p);setupProgress();b.setOnClickListener(v->startUrlDownload(input.getText().toString().trim(),"audio.mp3"));}
    void showQQ(){base("QQ 音乐");input=editor("粘贴 QQ 音乐分享链接",true);content.addView(input,new LinearLayout.LayoutParams(-1,dp(110)));Button b=button("识别链接");LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.topMargin=dp(14);content.addView(b,p);status=text("● 等待输入",14);status.setTextColor(muted());content.addView(status,new LinearLayout.LayoutParams(-1,dp(55)));b.setOnClickListener(v->{Matcher m=Pattern.compile("songmid=([A-Za-z0-9]+)").matcher(input.getText().toString());status.setText(m.find()?"● 已识别 SongMID："+m.group(1)+"\n当前版本暂不自动解析音频地址":"● 未找到 songmid");});}
    void showSearch(){base("综合搜索");input=editor("输入歌曲名",false);content.addView(input,new LinearLayout.LayoutParams(-1,dp(58)));Button b=button("搜索");LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.topMargin=dp(14);content.addView(b,p);TextView info=text("搜索结果区域\n\n当前 Version 1.0 优先完成网易云真实下载。\n后续再接入 QQ 音乐及其他来源。",15);info.setTextColor(muted());info.setPadding(0,dp(24),0,0);content.addView(info,new LinearLayout.LayoutParams(-1,-2));}
    void showHistory(){base("下载记录");String h=prefs.getString("history","");TextView t=text(h.isEmpty()?"暂无下载记录":h,15);t.setTextColor(muted());content.addView(t,new LinearLayout.LayoutParams(-1,-2));}
    void setupProgress(){progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);progress.setVisibility(View.GONE);LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(6));pp.topMargin=dp(20);content.addView(progress,pp);progressText=text("",13);progressText.setTextColor(muted());content.addView(progressText,new LinearLayout.LayoutParams(-1,dp(38)));status=text("● 等待输入",14);status.setTextColor(muted());content.addView(status,new LinearLayout.LayoutParams(-1,dp(42)));}

    void showDrawer(){
        Dialog d=new Dialog(this);d.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(18),dp(22),dp(18),dp(18));box.setBackground(bgDrawable(bg(),20));
        TextView h=text("音乐下载器",23);h.setTypeface(null,android.graphics.Typeface.BOLD);h.setPadding(dp(12),dp(10),dp(12),dp(22));box.addView(h,new LinearLayout.LayoutParams(-1,dp(64)));
        addMenu(box,"⌕  综合搜索",()->{d.dismiss();showSearch();});addMenu(box,"☁  网易云下载",()->{d.dismiss();buildNetease();});addMenu(box,"♫  QQ 音乐",()->{d.dismiss();showQQ();});addMenu(box,"↧  普通下载",()->{d.dismiss();showNormal();});addMenu(box,"▣  下载记录",()->{d.dismiss();showHistory();});
        Space sp=new Space(this);box.addView(sp,new LinearLayout.LayoutParams(1,0,1));addMenu(box,"⚙  设置",()->{d.dismiss();showSettings();});addMenu(box,"ⓘ  关于",()->{d.dismiss();showAbout();});
        d.setContentView(box);Window w=d.getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);w.setLayout(dp(310),-1);}d.show();if(w!=null)w.setLayout(Math.min(dp(330),(int)(getResources().getDisplayMetrics().widthPixels*.86f)),-1);
    }
    void addMenu(LinearLayout box,String s,Runnable r){Button b=button(s);b.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);b.setPadding(dp(14),0,dp(8),0);b.setOnClickListener(v->r.run());box.addView(b,new LinearLayout.LayoutParams(-1,dp(52)));}

    void showSettings(){base("设置");
        TextView a=text("外观",17);a.setTypeface(null,android.graphics.Typeface.BOLD);a.setPadding(dp(4),dp(10),0,dp(8));content.addView(a,new LinearLayout.LayoutParams(-1,dp(42)));
        RadioGroup rg=new RadioGroup(this);String[] names={"跟随系统","浅色","深色","炫彩"};for(int i=0;i<4;i++){RadioButton r=new RadioButton(this);r.setText(names[i]);r.setTextColor(fg());r.setTextSize(16);r.setButtonTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{ACCENT,muted()}));r.setChecked(themeMode==i);final int z=i;r.setOnClickListener(v->{themeMode=z;prefs.edit().putInt("theme",z).apply();buildSettings();});rg.addView(r,new RadioGroup.LayoutParams(-1,dp(48)));}content.addView(rg);
        TextView ic=text("应用图标",17);ic.setTypeface(null,android.graphics.Typeface.BOLD);ic.setPadding(dp(4),dp(18),0,dp(8));content.addView(ic,new LinearLayout.LayoutParams(-1,dp(48)));
        GridLayout grid=new GridLayout(this);grid.setColumnCount(3);grid.setUseDefaultMargins(false);int cur=prefs.getInt("icon",6);for(int i=1;i<=9;i++){LinearLayout cell=new LinearLayout(this);cell.setOrientation(LinearLayout.VERTICAL);cell.setGravity(Gravity.CENTER);ImageView iv=new ImageView(this);iv.setImageResource(getIcon(i));iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);GradientDrawable frame=bgDrawable(effectiveTheme()==1?Color.rgb(238,238,242):Color.rgb(34,34,38),16);if(i==cur)frame.setStroke(dp(2),ACCENT);cell.setBackground(frame);cell.setPadding(dp(8),dp(8),dp(8),dp(8));final int z=i;cell.setOnClickListener(v->changeIcon(z));TextView n=text(String.format(Locale.US,"%02d",i),12);n.setGravity(Gravity.CENTER);n.setTextColor(i==cur?ACCENT:muted());cell.addView(iv,new LinearLayout.LayoutParams(-1,dp(76)));cell.addView(n,new LinearLayout.LayoutParams(-1,dp(24)));GridLayout.LayoutParams cp=new GridLayout.LayoutParams();cp.width=0;cp.height=dp(108);cp.columnSpec=GridLayout.spec((i-1)%3,1f);cp.rowSpec=GridLayout.spec((i-1)/3);cp.setMargins(dp(4),dp(4),dp(4),dp(4));grid.addView(cell,cp);}content.addView(grid,new LinearLayout.LayoutParams(-1,dp(348)));TextView curv=text("当前图标："+String.format(Locale.US,"%02d",cur),14);curv.setTextColor(muted());content.addView(curv,new LinearLayout.LayoutParams(-1,dp(38)));
    }
    void buildSettings(){showSettings();}
    int getIcon(int i){return getResources().getIdentifier(String.format(Locale.US,"icon%02d",i),"drawable",getPackageName());}
    void changeIcon(int n){prefs.edit().putInt("icon",n).apply();PackageManager pm=getPackageManager();String pkg=getPackageName();for(int i=1;i<=9;i++){String cn=pkg+String.format(Locale.US,".Icon%02d",i);pm.setComponentEnabledSetting(new ComponentName(pkg,cn),i==n?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);}Toast.makeText(this,"已切换为图标 "+String.format(Locale.US,"%02d",n),Toast.LENGTH_SHORT).show();showSettings();}

    void showAbout(){base("关于");int current=prefs.getInt("icon",6);LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);hero.setGravity(Gravity.CENTER_HORIZONTAL);hero.setPadding(0,dp(10),0,dp(14));ImageView logo=new ImageView(this);logo.setImageResource(getIcon(current));logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);hero.addView(logo,new LinearLayout.LayoutParams(dp(120),dp(120)));TextView name=text("音乐下载器",24);name.setGravity(Gravity.CENTER);name.setTypeface(null,android.graphics.Typeface.BOLD);hero.addView(name,new LinearLayout.LayoutParams(-1,dp(40)));TextView ver=text("Version 1.0",15);ver.setGravity(Gravity.CENTER);ver.setTextColor(muted());hero.addView(ver,new LinearLayout.LayoutParams(-1,dp(30)));content.addView(hero,new LinearLayout.LayoutParams(-1,dp(210)));
        addPersonImage(com.moskao4.musicdownloader.R.drawable.logo_original,"moskao4","作者");addChatGPTPerson();TextView note=text("本页面中的应用图标会随设置中的选择自动变化。\n\nChatGPT / OpenAI 标志仅用于说明 AI 协作关系。",12);note.setTextColor(muted());note.setPadding(dp(12),dp(10),dp(12),0);content.addView(note,new LinearLayout.LayoutParams(-1,dp(70)));TextView copy=text("© 2026 moskao4",12);copy.setTextColor(muted());copy.setGravity(Gravity.CENTER);content.addView(copy,new LinearLayout.LayoutParams(-1,dp(42)));}
    void addPersonImage(int res,String name,String role){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(12),dp(7),dp(12),dp(7));ImageView av=new ImageView(this);av.setImageResource(res);av.setScaleType(ImageView.ScaleType.CENTER_CROP);av.setClipToOutline(true);if(Build.VERSION.SDK_INT>=21){av.setOutlineProvider(new ViewOutlineProvider(){public void getOutline(View v,android.graphics.Outline o){o.setOval(0,0,v.getWidth(),v.getHeight());}});}row.addView(av,new LinearLayout.LayoutParams(dp(58),dp(58)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);TextView n=text(name,18);TextView r=text(role,13);r.setTextColor(muted());tx.addView(n,new LinearLayout.LayoutParams(-1,dp(34)));tx.addView(r,new LinearLayout.LayoutParams(-1,dp(24)));row.addView(tx,new LinearLayout.LayoutParams(0,dp(70),1));content.addView(row,new LinearLayout.LayoutParams(-1,dp(74)));}
    void addChatGPTPerson(){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(12),dp(7),dp(12),dp(7));TextView av=text("◎",30);av.setGravity(Gravity.CENTER);av.setTextColor(fg());av.setBackground(bgDrawable(effectiveTheme()==1?Color.rgb(235,235,238):Color.rgb(34,34,38),29));row.addView(av,new LinearLayout.LayoutParams(dp(58),dp(58)));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);TextView n=text("ChatGPT",18);TextView r=text("AI 协作者",13);r.setTextColor(muted());tx.addView(n,new LinearLayout.LayoutParams(-1,dp(34)));tx.addView(r,new LinearLayout.LayoutParams(-1,dp(24)));row.addView(tx,new LinearLayout.LayoutParams(0,dp(70),1));content.addView(row,new LinearLayout.LayoutParams(-1,dp(74)));}

    void startNetease(){String s=input.getText().toString().trim();Matcher id=Pattern.compile("song\\?id=(\\d+)").matcher(s);if(!id.find()){Toast.makeText(this,"没有识别到网易云歌曲 ID",Toast.LENGTH_SHORT).show();status.setText("● 未识别到 song?id=歌曲ID");return;}String sid=id.group(1);String name="网易云歌曲";Matcher nm=Pattern.compile("《\\s*([^《》\\r\\n]+?)(?:（|\\(|》)").matcher(s);if(nm.find())name=nm.group(1).trim();name=name.replaceAll("[\\\\/:*?\"<>|]","_");String url="https://music.163.com/song/media/outer/url?id="+sid;status.setText("● 正在准备："+name);startUrlDownload(url,name+".mp3");}
    void startUrlDownload(String url,String filename){if(url.isEmpty()){Toast.makeText(this,"请输入链接",Toast.LENGTH_SHORT).show();return;}setupIfMissing();progress.setVisibility(View.VISIBLE);progress.setProgress(0);status.setText("● 下载中");progressText.setText("正在连接…");Executors.newSingleThreadExecutor().execute(()->{HttpURLConnection c=null;Uri uri=null;try{URL u=new URL(url);c=(HttpURLConnection)u.openConnection();c.setInstanceFollowRedirects(true);c.setConnectTimeout(20000);c.setReadTimeout(60000);c.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36");c.setRequestProperty("Referer","http://music.163.com/");c.setRequestProperty("Accept","audio/mpeg,audio/*,*/*;q=0.8");int code=c.getResponseCode();if(code<200||code>=400)throw new IOException("HTTP "+code);long len=c.getContentLengthLong();InputStream in=new BufferedInputStream(c.getInputStream());if(len==0 && c.getContentType()!=null && !c.getContentType().contains("audio")) throw new IOException("非音频文件");OutputStream out;if(Build.VERSION.SDK_INT>=29){ContentValues v=new ContentValues();v.put(MediaStore.Downloads.DISPLAY_NAME,filename);v.put(MediaStore.Downloads.MIME_TYPE,"audio/mpeg");v.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS);v.put(MediaStore.Downloads.IS_PENDING,1);uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,v);if(uri==null)throw new IOException("无法创建 Download 文件");out=getContentResolver().openOutputStream(uri);}else{File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);if(!dir.exists()&&!dir.mkdirs())throw new IOException("无法创建 Download 文件夹");out=new FileOutputStream(new File(dir,filename));}byte[] buf=new byte[16384];int n;long done=0,start=System.currentTimeMillis();while((n=in.read(buf))!=-1){out.write(buf,0,n);done+=n;long elapsed=Math.max(1,System.currentTimeMillis()-start);int pct=len>0?(int)Math.min(100,done*100/len):0;double kb=done*1000.0/elapsed/1024.0;final int fp=pct;final double fk=kb;runOnUiThread(()->{progress.setProgress(fp);progressText.setText(String.format(Locale.US,"%d%% · %.1f KB/s",fp,fk));});}out.close();in.close();if(uri!=null){ContentValues v=new ContentValues();v.put(MediaStore.Downloads.IS_PENDING,0);getContentResolver().update(uri,v,null,null);}saveHistory(filename);runOnUiThread(()->{status.setText("● 下载完成："+filename);progress.setProgress(100);Toast.makeText(this,"已保存到 Download/"+filename,Toast.LENGTH_LONG).show();});}catch(Exception e){if(uri!=null&&Build.VERSION.SDK_INT>=29){try{ContentValues v=new ContentValues();v.put(MediaStore.Downloads.IS_PENDING,0);getContentResolver().update(uri,v,null,null);}catch(Exception ignored){}}final String msg=e.getMessage()==null?e.toString():e.getMessage();runOnUiThread(()->status.setText("● 下载失败："+msg));}finally{if(c!=null)c.disconnect();}});}
    void setupIfMissing(){if(progress==null||progress.getParent()==null){setupProgress();}}
    void saveHistory(String filename){String old=prefs.getString("history","");prefs.edit().putString("history","♪ "+filename+"\n   音乐下载器 · MP3\n\n"+old).apply();}
}
