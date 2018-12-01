// if you put your Activities files to another folder than the default one. You need to import the
// com.example.yourproject.R (this is your project R file NOT Android.R file) to ALL activities using R.
package ro.sapientia.ms.sapiadvertiser.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.adapters.AdvertisementsRecyclerAdapter;
import ro.sapientia.ms.sapiadvertiser.models.Advertisement;
import ro.sapientia.ms.sapiadvertiser.utils.Constants;


public class MyAdvertisementsActivity extends AppCompatActivity {

    private static final String TAG = "MyAdvertisements";

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.advert_list_view)
    RecyclerView advertListView;

    private List<Advertisement> advList;
    private String mUserId;
    private AdvertisementsRecyclerAdapter advertisementsRecyclerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAdvertisementsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_advertisements);

        ButterKnife.bind(MyAdvertisementsActivity.this);
        advList = new ArrayList<>();
        advertListView.setHasFixedSize(true);

        advertListView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAdvertisementsRef = mFirebaseDatabase.getReference().child(Constants.ADVERTISEMENTS_CHILD);
        mUserId = mAuth.getCurrentUser().getUid();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMyAdvertisements();

    }

    public void getMyAdvertisements() {

        Query recentList = mAdvertisementsRef.orderByChild("creator_user/id").equalTo(mUserId);

        recentList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                advList.clear();
                for (DataSnapshot advertisementSnapshot : dataSnapshot.getChildren()) {

                    Advertisement adv = advertisementSnapshot.getValue(Advertisement.class);
                    advList.add(adv);

                    advertisementsRecyclerAdapter = new AdvertisementsRecyclerAdapter(advList, MyAdvertisementsActivity.this);
                    advertListView.setAdapter(advertisementsRecyclerAdapter);
                    advertisementsRecyclerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent mainScreenIntent = new Intent(MyAdvertisementsActivity.this, AdvertisementListActivity.class);
                    startActivity(mainScreenIntent);
                    finish();
                    break;
                case R.id.navigation_profile:
                    Intent profileScreenIntent = new Intent(MyAdvertisementsActivity.this, ProfileActivity.class);
                    startActivity(profileScreenIntent);
                    finish();
                    break;
                case R.id.navigation_new_blog:
                    Intent addAdvertisementIntent = new Intent(MyAdvertisementsActivity.this, AddAdvertisementActivity.class);
                    startActivity(addAdvertisementIntent);
                    finish();
                    break;
            }
            return false;
        }
    };
}
