package fi.seamk.winhawille;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.tolstykh.textviewrichdrawable.TextViewRichDrawable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WinhaCourse extends AbstractItem<WinhaCourse, WinhaCourse.ViewHolder> {

    private String name;
    private String code;
    private String credits;
    private String grade;
    private String teacher;
    private String date;

    public WinhaCourse() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateLong() {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Long longEndDate = 0L;
        try { longEndDate = format.parse(date).getTime(); } catch (ParseException e) { e.printStackTrace(); }
        return longEndDate;
    }

    @Override
    public int getType() {
        return R.id.fa_winha_course_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.card_winha_course;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.name.setText(name);
        holder.code.setText(code);
        holder.credits.setText(credits);
        holder.teacher.setText(teacher);
        holder.date.setText(date);
        holder.grade.setText(grade);

        switch (grade){
            case "0":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.MATERIAL_COLORS[2]));
                break;
            case "1":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.COLORFUL_COLORS[1]));
                break;
            case "2":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.COLORFUL_COLORS[2]));
                break;
            case "3":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.MATERIAL_COLORS[3]));
                break;
            case "4":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.MATERIAL_COLORS[0]));
                break;
            case "5":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.JOYFUL_COLORS[0]));
                break;
            case "H":
                ImageViewCompat.setImageTintList(holder.image, ColorStateList.valueOf(ColorTemplate.JOYFUL_COLORS[0]));
                break;
        }
    }

    @Override
    public void unbindView(@NonNull ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.code.setText(null);
        holder.credits.setText(null);
        holder.teacher.setText(null);
        holder.date.setText(null);
        holder.grade.setText(null);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.code)
        TextViewRichDrawable code;
        @BindView(R.id.credits)
        TextViewRichDrawable credits;
        @BindView(R.id.teacher)
        TextViewRichDrawable teacher;
        @BindView(R.id.time)
        TextViewRichDrawable date;
        @BindView(R.id.image)
        AppCompatImageView image;
        @BindView(R.id.grade)
        TextView grade;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
