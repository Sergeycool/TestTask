package test.ainsoft.net.testtask;
//

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    private OnCheckItemListener mListener;
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Admin> objects;

    BoxAdapter(Context context, ArrayList<Admin> products) {
        if (context instanceof OnCheckItemListener)
            mListener = (OnCheckItemListener) context;
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row, parent, false);
        }

        Admin p = getAdmin(position);

        // заполняем View в пункте списка данными : наименование, картинка
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.getName());
        ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.getImage());


        CheckBox cbBuy = view.findViewById(R.id.cbBox);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                getAdmin((Integer) buttonView.getTag()).setBox(isChecked);
                mListener.onChecked(isChecked, position);
            }
        });
        // пишем позицию
        cbBuy.setTag(position);

        cbBuy.setChecked(p.isBox());
        return view;
    }


    Admin getAdmin(int position) {
        return ((Admin) getItem(position));
    }


    ArrayList<Admin> getBox() {
        ArrayList<Admin> box = new ArrayList<Admin>();
        for (Admin p : objects) {
            // если в корзине
            if (p.isBox())
                box.add(p);
        }
        return box;
    }

    public interface OnCheckItemListener {

        void onChecked(boolean isChecked, int position);


    }
}