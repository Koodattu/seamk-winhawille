package fi.seamk.winhawille;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.SlideInOutRightAnimator;
import com.tolstykh.textviewrichdrawable.TextViewRichDrawable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.input_username)
    TextInputEditText inputUsername;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.rl_loading)
    RelativeLayout rlLoading;
    @BindView(R.id.tv_gpa)
    TextView tvGpa;
    @BindView(R.id.ll_results)
    LinearLayout llResults;
    @BindView(R.id.b_login)
    AppCompatButton bLogin;
    @BindView(R.id.tvLoadingInfo)
    TextView tvLoadingInfo;
    @BindView(R.id.tv_credits)
    TextView tvCredits;
    @BindView(R.id.cbUsername)
    AppCompatCheckBox cbUsername;
    @BindView(R.id.cbPassword)
    AppCompatCheckBox cbPassword;
    @BindView(R.id.tv_results_header)
    TextView tvResultsHeader;
    @BindView(R.id.rv_courses)
    RecyclerView rvCourses;
    @BindView(R.id.graph)
    BarChart graph;
    @BindView(R.id.spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.error_view)
    TextViewRichDrawable errorView;

    String winhaUser = "";
    String winhaPass = "";

    ItemAdapter<IItem> itemAdapter;
    FastAdapter<IItem> fastAdapter;

    List<IItem> defaultItems = new ArrayList<>();
    List<IItem> orderedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setTitle(getString(R.string.login));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0, true);
        View v = spinner.getSelectedView();
        ((TextView)v).setTextColor(Color.WHITE);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.WHITE);

                switch (position) {
                    case 0: // default
                        orderedItems = new ArrayList<>(defaultItems);
                        itemAdapter.clear();
                        itemAdapter.add(orderedItems);
                        break;
                    case 1: // newest by date
                        orderedItems = new ArrayList<>(defaultItems);
                        Collections.sort(orderedItems, new Comparator<IItem>() {
                            public int compare(IItem obj1, IItem obj2) {
                                if (((WinhaCourse) obj1).getDateLong() == ((WinhaCourse) obj2).getDateLong()) {
                                    return ((WinhaCourse) obj1).getName().compareToIgnoreCase(((WinhaCourse) obj2).getName()); // alphabetical ordering
                                } else {
                                    return ((WinhaCourse) obj1).getDateLong() > ((WinhaCourse) obj2).getDateLong() ? -1 : 1; // dates descending
                                }
                            }
                        });
                        itemAdapter.clear();
                        itemAdapter.add(orderedItems);
                        break;
                    case 2: // oldest by date
                        orderedItems = new ArrayList<>(defaultItems);
                        Collections.sort(orderedItems, new Comparator<IItem>() {
                            public int compare(IItem obj1, IItem obj2) {
                                if (((WinhaCourse) obj1).getDateLong() == ((WinhaCourse) obj2).getDateLong()) {
                                    return ((WinhaCourse) obj1).getName().compareToIgnoreCase(((WinhaCourse) obj2).getName());
                                } else {
                                    return ((WinhaCourse) obj1).getDateLong() < ((WinhaCourse) obj2).getDateLong() ? -1 : 1;
                                }
                            }
                        });
                        itemAdapter.clear();
                        itemAdapter.add(orderedItems);
                        break;
                    case 3: // highest grade first
                        orderedItems = new ArrayList<>(defaultItems);
                        Collections.sort(orderedItems, new Comparator<IItem>() {
                            public int compare(IItem obj1, IItem obj2) {
                                if (((WinhaCourse) obj1).getGrade().equals(((WinhaCourse) obj2).getGrade())) {
                                    return ((WinhaCourse) obj1).getName().compareToIgnoreCase(((WinhaCourse) obj2).getName()); // alphabetical ordering
                                } else {
                                    return -((WinhaCourse) obj1).getGrade().compareToIgnoreCase(((WinhaCourse) obj2).getGrade()); // higher grade first
                                }
                            }
                        });
                        itemAdapter.clear();
                        itemAdapter.add(orderedItems);
                        break;
                    case 4: // lowest grade
                        orderedItems = new ArrayList<>(defaultItems);
                        Collections.sort(orderedItems, new Comparator<IItem>() {
                            public int compare(IItem obj1, IItem obj2) {
                                if (((WinhaCourse) obj1).getGrade().equals(((WinhaCourse) obj2).getGrade())) {
                                    return ((WinhaCourse) obj1).getName().compareToIgnoreCase(((WinhaCourse) obj2).getName());
                                } else {
                                    return ((WinhaCourse) obj1).getGrade().compareToIgnoreCase(((WinhaCourse) obj2).getGrade());
                                }
                            }
                        });
                        itemAdapter.clear();
                        itemAdapter.add(orderedItems);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        rvCourses.setItemAnimator(new SlideInOutRightAnimator(rvCourses));
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        rvCourses.setAdapter(fastAdapter);

        inputUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inputPassword.requestFocus();
                }
                return true;
            }
        });

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryLogin();
                }
                return true;
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });

        rlLoading.setAlpha(0f);
        llResults.setAlpha(0f);
        llResults.setVisibility(View.GONE);

        winhaUser = getSettingStringValue("winhaUser", "", this);
        winhaPass = getSettingStringValue("winhaPass", "", this);

        if (!winhaUser.equals("")) {
            inputUsername.setText(winhaUser);
        } else {
            cbUsername.setChecked(false);
        }
        if (!winhaPass.equals("")) {
            inputPassword.setText(winhaPass);
        } else {
            cbPassword.setChecked(false);
        }

        graph.setDrawGridBackground(false);
        graph.setDrawBorders(false);
        graph.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        graph.setDescription(description);
        graph.setTouchEnabled(false);
        graph.getAxisLeft().setAxisMinimum(0f);
        graph.getAxisRight().setAxisMinimum(0f);
        graph.getAxisLeft().setDrawAxisLine(false);
        graph.getAxisRight().setDrawAxisLine(false);
        graph.getAxisLeft().setDrawLabels(false);
        graph.getAxisRight().setDrawLabels(false);
        graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graph.getXAxis().setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
        graph.setExtraBottomOffset(4f);
        graph.getXAxis().setDrawGridLines(false);
        graph.getAxisLeft().setDrawGridLines(false);
        graph.getAxisRight().setDrawGridLines(false);
        graph.getXAxis().setAxisLineWidth(2f);
        graph.getXAxis().setAxisLineColor(R.color.cardview_dark_background);
        graph.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        graph.getRenderer().getPaintRender().setShadowLayer(3, 5, 3, Color.GRAY);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if (!getSettingBoolValue("agreementComplete", false, this)){
            Intent intent = new Intent(this, ActivityAgreement.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        llLogin.requestFocus();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        if (!cbUsername.isChecked()) {
            saveSettingStringValue("winhaUser", "", this);
        }
        if (!cbPassword.isChecked()) {
            saveSettingStringValue("winhaPass", "", this);
        }
        super.onStop();
    }

    public static String getSettingStringValue(String settingName, String defaultValue, Context context){
        SharedPreferences settings = context.getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
        return settings.getString(settingName, defaultValue);
    }

    public static void saveSettingStringValue(String settingName, String writeValue, Context context){
        SharedPreferences settings = context.getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = settings.edit();
        mEditor.putString(settingName, writeValue);
        mEditor.apply();
    }

    public static boolean getSettingBoolValue(String settingName, boolean defaultValue, Context context){
        SharedPreferences settings = context.getSharedPreferences("ApplicationPreferences", Context.MODE_PRIVATE);
        return settings.getBoolean(settingName, defaultValue);
    }


    void tryLogin() {
        if (inputUsername.getText().toString().isEmpty() || inputPassword.getText().toString().isEmpty()) {
            Toasty.error(this, getString(R.string.fill_empty_fields), Toast.LENGTH_SHORT, true).show();
        } else {
            LoginFieldsEnableDisable(false);
            rlLoading.animate().alpha(1.0f).setDuration(500).start();
            errorView.setVisibility(View.GONE);

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WinhaLoginTask winhaLoginTask = new WinhaLoginTask();
                    winhaLoginTask.execute();
                }
            }, 500);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (cbUsername.isChecked()) {
            saveSettingStringValue("winhaUser", inputUsername.getText().toString(), this);
        }
        if (cbPassword.isChecked()) {
            saveSettingStringValue("winhaPass", inputPassword.getText().toString(), this);
        }
    }

    public void LoginFieldsEnableDisable(boolean value){
        inputUsername.setEnabled(value);
        cbUsername.setEnabled(value);
        inputPassword.setEnabled(value);
        cbPassword.setEnabled(value);
        bLogin.setEnabled(value);
    }

    public void SetTitle(String title){
        this.setTitle(title);
    }

    private class WinhaLoginTask extends AsyncTask<Void, String, Void> {

        String gName = "NO_NAME";
        String gCredits = "NO_CREDITS";
        String gGpa = "NO_GPA";

        Exception error = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String loginPageUrl = "https://wille.epedu.fi/WinhaWilleSeamk/logon.asp";
                String personPageUrl = "https://wille.epedu.fi/WinhaWilleSeamk/henkilo.asp";
                String loginPageSubmitUrl = "https://wille.epedu.fi/WinhaWilleSeamk/logon.asp?dfUsername?dfPassword?dfUsernameHuoltaja";
                String sidePageEngUrl = "https://wille.epedu.fi/WinhaWilleSeamk/emainval.asp";
                String sidePageUrl = "https://wille.epedu.fi/WinhaWilleSeamk/mainval.asp";
                String coursesHOPSPageUrl = "https://wille.epedu.fi/WinhaWilleSeamk/eWilleNetLink.asp?Link=https://wille.epedu.fi/WilleNetSeamk/HopsSuoritukset.aspx&Hyv=1&Opjakso=&ArvPvm=";
                //String loginPageUrl = "https://secure.puv.fi/wille/logon.asp";
                //String personPageUrl = "https://secure.puv.fi/wille/henkilo.asp";
                //String loginPageSubmitUrl = "https://secure.puv.fi/wille/logon.asp?dfUsername?dfPassword?dfUsernameHuoltaja";
                //String sidePageUrl = "https://secure.puv.fi/wille/mainval.asp";
                //String coursesHOPSPageUrl = "https://secure.puv.fi/wille/eWilleNetLink.asp?Link=https://secure.puv.fi/willenet/HopsSuoritukset.aspx&Hyv=1&Opjakso=&ArvPvm=";

                publishProgress(getString(R.string.fetching_login_page));

                Connection.Response loginPage = Jsoup.connect(loginPageUrl)
                        .method(Connection.Method.GET)
                        .execute();

                publishProgress(getString(R.string.logging_in));

                Connection.Response mainPage = Jsoup.connect(loginPageSubmitUrl)
                        .method(Connection.Method.POST)
                        .data("cookieexists", "false")
                        .data("dfUsernameHidden", inputUsername.getText().toString())
                        .data("dfPasswordHidden", inputPassword.getText().toString())
                        .cookies(loginPage.cookies())
                        .execute();

                publishProgress(getString(R.string.fetching_side_page));

                Connection.Response sidePage = Jsoup.connect(sidePageUrl)
                        .method(Connection.Method.GET)
                        .cookies(loginPage.cookies())
                        .execute();

                publishProgress(getString(R.string.fetching_personal_data));

                Document personPage = Jsoup.connect(personPageUrl)
                        .cookies(loginPage.cookies())
                        .get();

                Element personTable = personPage.selectFirst("table");
                Elements personRows = personTable.select("tr");

                gName = personRows.get(3).select("td").get(1).text();

                publishProgress(getString(R.string.loading_hops));

                Document resultsPage = Jsoup.connect(coursesHOPSPageUrl)
                        .data("cookieexists", "false")
                        .data("rbRajaus", "rbSuoritukset")
                        .data("dfOpjakso", "")
                        .data("dfArviointipvm", "")
                        .cookies(loginPage.cookies())
                        .post();

                Element table = resultsPage.selectFirst("table");
                Elements rows = table.select("tr");

                List<IItem> winhaCourses = new ArrayList<>();

                for (int i = 1; i < rows.size() - 2; i++) {
                    if (!rows.get(i).html().equals("<td></td>")) {
                        WinhaCourse winhaCourse = new WinhaCourse();

                        Elements elements = rows.get(i).select("td");

                        winhaCourse.setName(elements.get(1).select("a").get(0).text());
                        winhaCourse.setCode(elements.get(1).select("a").get(1).text());
                        winhaCourse.setCredits(elements.get(4).text());
                        winhaCourse.setGrade(elements.get(7).text());
                        winhaCourse.setTeacher(elements.get(8).text());
                        winhaCourse.setDate(elements.get(9).selectFirst("a").text());

                        winhaCourses.add(winhaCourse);
                    }
                }

                defaultItems = new ArrayList<>(winhaCourses);

                publishProgress(getString(R.string.calculating_scores));

                int credits = 0;
                double gpa = 0;
                int gradesCount = 0;

                int grade0 = 0;
                int grade1 = 0;
                int grade2 = 0;
                int grade3 = 0;
                int grade4 = 0;
                int grade5 = 0;

                for (IItem iItem : winhaCourses) {
                    WinhaCourse winhaCourse = (WinhaCourse) iItem;
                    if (!winhaCourse.getCredits().startsWith("0")) { // in case course gives no credits, we wont use it for calculating gpa
                        if (winhaCourse.getGrade().equals("H") || winhaCourse.getGrade().equals("S") || Integer.parseInt(winhaCourse.getGrade()) != 0) {
                            credits += Integer.parseInt(winhaCourse.getCredits().substring(0, winhaCourse.getCredits().indexOf(",")));
                        }
                        if (winhaCourse.getGrade().matches("[0-9]+")) {
                            gpa += Double.parseDouble(winhaCourse.getGrade());
                            gradesCount++;

                            switch (Integer.parseInt(winhaCourse.getGrade())) {
                                case 0:
                                    grade0++;
                                    break;
                                case 1:
                                    grade1++;
                                    break;
                                case 2:
                                    grade2++;
                                    break;
                                case 3:
                                    grade3++;
                                    break;
                                case 4:
                                    grade4++;
                                    break;
                                case 5:
                                    grade5++;
                                    break;
                            }
                        }
                    }
                }

                List<Integer> grades = new ArrayList<>();
                grades.add(grade0);
                grades.add(grade1);
                grades.add(grade2);
                grades.add(grade3);
                grades.add(grade4);
                grades.add(grade5);

                gCredits = Integer.toString(credits);
                gGpa = new DecimalFormat("#.##").format(gpa / gradesCount);

                List<BarEntry> entries = new ArrayList<>();

                for (int i = 0; i < grades.size(); i++) {
                    entries.add(new BarEntry(i, grades.get(i)));
                }

                BarDataSet dataSet = new BarDataSet(entries, "");

                dataSet.setColors(ColorTemplate.MATERIAL_COLORS[2], ColorTemplate.COLORFUL_COLORS[1], ColorTemplate.COLORFUL_COLORS[2], ColorTemplate.MATERIAL_COLORS[3], ColorTemplate.MATERIAL_COLORS[0], ColorTemplate.JOYFUL_COLORS[0]);
                dataSet.setValueTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics()));
                BarData barData = new BarData(dataSet);
                barData.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return new DecimalFormat("#").format(value);
                    }
                });

                graph.setData(barData);
                graph.invalidate();

                orderedItems = new ArrayList<>(defaultItems);
                Collections.sort(orderedItems, new Comparator<IItem>() {
                    public int compare(IItem obj1, IItem obj2) {
                        if (((WinhaCourse) obj1).getDateLong() == ((WinhaCourse) obj2).getDateLong()) {
                            return ((WinhaCourse) obj1).getName().compareToIgnoreCase(((WinhaCourse) obj2).getName()); // alphabetical ordering
                        } else {
                            return ((WinhaCourse) obj1).getDateLong() > ((WinhaCourse) obj2).getDateLong() ? -1 : 1; // dates descending
                        }
                    }
                });
                itemAdapter.clear();

                publishProgress(getString(R.string.done));

            } catch (Exception e) {
                e.printStackTrace();
                error = e;
            }
            itemAdapter.add(orderedItems);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... status) {
            super.onProgressUpdate(status);
            tvLoadingInfo.setText(status[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (error != null) {
                LoginFieldsEnableDisable(true);
                rlLoading.setAlpha(0f);
                errorView.setVisibility(View.VISIBLE);
                errorView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_warning, 0, 0);
                errorView.getCompoundDrawables()[1].setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.extrasGoldColor), PorterDuff.Mode.SRC_IN));

                if (error.toString().contains("Status=500")) {
                    errorView.setText(R.string.winha_invalid_password);
                } else if (error.toString().contains("SocketTimeoutException")) {
                    errorView.setText(R.string.winha_time_out);
                } else if (error.toString().contains("Unable to resolve host")) {
                    errorView.setText(R.string.winha_no_internet);
                } else {
                    errorView.setText(getString(R.string.winha_unkown_error) + error.getMessage());
                }

            } else {

                SetTitle(getString(R.string.results));

                tvResultsHeader.setText(gName);
                tvGpa.setText(gGpa);
                tvCredits.setText(gCredits);

                spinner.setSelection(1);

                llLogin.animate().alpha(0.0f).setDuration(500).start();

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llLogin.setVisibility(View.GONE);
                        llResults.setVisibility(View.VISIBLE);
                        llResults.setAlpha(0.0f);
                        llResults.animate().alpha(1.0f).setDuration(500).start();
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                }, 500);
            }
        }
    }
}
