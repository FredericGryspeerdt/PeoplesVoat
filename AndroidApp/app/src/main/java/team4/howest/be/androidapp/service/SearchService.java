package team4.howest.be.androidapp.service;

import java.util.ArrayList;
import java.util.List;

import team4.howest.be.androidapp.model.Submission;

/**
 * Created by Srg on 6/12/2015.
 */
public class SearchService {
    public static List<Submission> searchSubmissionsForText(List<Submission> submissionsListFull,String text)
    {
        text = text.toLowerCase();
        String title;
        Object content;
        List<Submission> submissionList = new ArrayList<>();

        for(Submission submission: submissionsListFull)
        {
            title = submission.getTitle().toLowerCase();
            content = submission.getContent(); //kan soms 'null' zijn (bij links naar foto's)

            if(content!=null) //zoeken in titel en content als er een content is; anders enkel in titel
            {
                if(title.contains(text) || content.toString().contains(text) )
                {
                    submissionList.add(submission);
                }
            }
            else
            {
                if(title.contains(text))
                {
                    submissionList.add(submission);
                }
            }
        }

        return submissionList;
    }
}
