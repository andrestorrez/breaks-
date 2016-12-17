package co.icoms.breaks;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import co.icoms.breaks.models.Request;
import co.icoms.breaks.models.User;

import static co.icoms.breaks.RequestDetailActivity.REPLY_FRAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestDetailFragment extends Fragment {

    TextView messageView;
    TextView commentsView;
    TextView messageTitleView;
    TextView commentsTitleView;
    TextView statusView;
    Request mRequest;
    User current_user;
    View mRootView;

    public RequestDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_request_detail, container, false);

        current_user = User.current_user(getActivity());
        Bundle args = getArguments();
        mRequest =  DataHolder.getInstance().getRequests().get(args.getInt("request_position", 0));

        messageView = (TextView) mRootView.findViewById(R.id.message);
        commentsView = (TextView) mRootView.findViewById(R.id.comments);

        messageTitleView = (TextView) mRootView.findViewById(R.id.message_title);
        commentsTitleView = (TextView) mRootView.findViewById(R.id.comments_title);

        statusView = (TextView) mRootView.findViewById(R.id.status);

        hideShowMsgCom();

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideShowMsgCom();
    }

    private void changeFragment(boolean checkbox){
        RequestReplyFragment reply = new RequestReplyFragment();
        Bundle args = getArguments();
        args.putBoolean("checkbox", checkbox);
        reply.setArguments(args);

        FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
        trx.replace(R.id.fragment, reply, REPLY_FRAG);
        trx.addToBackStack(null);
        trx.commit();
    }

    private void hideShowMsgCom(){
        if (current_user.isUser() || mRequest.isApproved() || mRequest.isRejected()){
            mRootView.findViewById(R.id.actions_container).setVisibility(View.GONE);
        }else{
            mRootView.findViewById(R.id.actions_container).setVisibility(View.VISIBLE);
            Button approveBtnView = (Button) mRootView.findViewById(R.id.approve);
            approveBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeFragment(true);
                }
            });

            Button rejectBtnView = (Button) mRootView.findViewById(R.id.reject);
            rejectBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeFragment(false);
                }
            });
        }

        if (mRequest.getMessage().isEmpty()) {
            messageView.setVisibility(View.GONE);
            messageTitleView.setVisibility(View.GONE);
        }else{
            messageView.setText(mRequest.getMessage());
            messageView.setVisibility(View.VISIBLE);
            messageTitleView.setVisibility(View.VISIBLE);
        }


        if (mRequest.getComments().isEmpty()) {
            commentsView.setVisibility(View.GONE);
            commentsTitleView.setVisibility(View.GONE);
        }else {
            commentsView.setText(mRequest.getComments());
            commentsView.setVisibility(View.VISIBLE);
            commentsTitleView.setVisibility(View.VISIBLE);
        }

        statusView.setText(Request.getRequestStatus(getContext(), mRequest));
    }

}
