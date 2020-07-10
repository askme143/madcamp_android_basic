<<<<<<< HEAD:app/src/main/java/com/example/practice/contacts/Fragment1.java
package com.example.practice.contacts;
=======
package com.example.practice.ui.free;
>>>>>>> origin/master:app/src/main/java/com/example/practice/ui/free/Fragment3.java

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.practice.R;

<<<<<<< HEAD:app/src/main/java/com/example/practice/contacts/Fragment1.java
public class :w:Fragment1 extends Fragment {
=======
public class Fragment3 extends Fragment {
>>>>>>> origin/master:app/src/main/java/com/example/practice/ui/free/Fragment3.java
    ViewGroup viewGroup;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        return viewGroup;
    }

}
