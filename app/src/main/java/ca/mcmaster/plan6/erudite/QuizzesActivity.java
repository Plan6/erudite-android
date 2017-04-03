package ca.mcmaster.plan6.erudite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.mcmaster.plan6.erudite.fetch.FetchAPIData;
import ca.mcmaster.plan6.erudite.fetch.QuizAbstraction;

public class QuizzesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizzes_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Model Variables
        final QuizAbstraction qa = new QuizAbstraction();

        //View Variables
        //Question fields
        final TextView question1 = (TextView) findViewById(R.id.question1);
        final TextView question2 = (TextView) findViewById(R.id.question2);
        final TextView question3 = (TextView) findViewById(R.id.question3);

        //Answer Fields
        final EditText answer1   = (EditText) findViewById(R.id.answer1);
        final EditText answer2   = (EditText) findViewById(R.id.answer2);
        final EditText answer3   = (EditText) findViewById(R.id.answer3);

        //Buttons
        final Button submitButton = (Button) findViewById(R.id.submit);


        try {
            JSONObject data = new JSONObject()
                    .put("url", "http://erudite.ml/course-quiz-demo")
                    .put("auth_token", DataStore.load(R.string.pref_key_token));

            new FetchAPIData() {
                @Override
                protected void onFetch(JSONObject data) {

                    //Query model
                    qa.setRawData(data.toString());
                    final ArrayList<String> questions = qa.getQuestions();
                    final ArrayList<String> answers   = qa.getAnswers();

                    //Populate View
                    question1.setText(questions.get(0));
                    question2.setText(questions.get(1));
                    question3.setText(questions.get(2));

                    final ArrayList<EditText> studentAnswers = new ArrayList<EditText>();
                    studentAnswers.add(answer1);
                    studentAnswers.add(answer2);
                    studentAnswers.add(answer3);

                    //Submit
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int numberCorrect = submitButtonPressed(studentAnswers, answers);
                            Log.v("Number Correct", String.valueOf(numberCorrect));
                        }
                    });
                }
            }.fetch(data);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private int submitButtonPressed(ArrayList<EditText> studentAnswers, ArrayList<String> answers){
        String[] studentAnswersText = extractStudentAnswers(studentAnswers);
        int numberCorrect = 0;
        for(int i = 0; i < studentAnswersText.length; i++){
            if(studentAnswersText[i].equalsIgnoreCase(answers.get(i))){
                numberCorrect++;
            }
        }
        return numberCorrect;
    }

    private String[] extractStudentAnswers(ArrayList<EditText> studentAnswers){
        String[] answers = new String[studentAnswers.size()];
        for(int i = 0; i < answers.length; i++){
            answers[i] = studentAnswers.get(i).getText().toString();
        }
        return answers;
    }
}