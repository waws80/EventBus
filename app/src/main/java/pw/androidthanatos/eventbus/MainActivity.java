package pw.androidthanatos.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import pw.androidthanatos.library.EventBus;
import pw.androidthanatos.library.Subscribe;
import pw.androidthanatos.library.ThreadMode;


public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        tv= (TextView) findViewById(R.id.textView);
    }

    public void post(View view){

       EventBus.getDefault().post(new User("zhangsan","25"));

    }


    public void jump(View view){

        startActivity(new Intent(this,SecondActivity.class));

    }

    @Subscribe
    public void receiver(User user){
        tv.setText(user.toString()+"\nthread:"+Thread.currentThread().getName());
    }

    @Subscribe(ThreadMode.MAIN)
    public void receiver(String string){
        tv.setText(string+"\nthread:"+Thread.currentThread().getName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unRegister(this);
    }
}
