package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.CommitDetailViewPagerFragment;

/**
 * commit详情
 * 
 * @created 2014-06-12
 * @author 火蚁
 *
 */
public class CommitDetailActivity extends BaseActionBarActivity {
	
	private FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_detail);
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mFragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		Project mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		Commit mCommit = (Commit) intent.getSerializableExtra(Contanst.COMMIT);
		mActionBar.setTitle("提交" + mCommit.getId().substring(0, 9));
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/" + mProject.getName());
		
        if (null == savedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.commit_content, CommitDetailViewPagerFragment.newInstance(mProject, mCommit)).commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}
