package com.mycompany.servicetime;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

/**
 * Created by szhx on 4/4/2016.
 */
public class MockCHApplication extends CHApplication {

    @Override
    protected Context createContext() {
        RenamingDelegatingContext mockContext =
                new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(),
                        "test_");
        mockContext.makeExistingFilesAndDbsAccessible();

        return mockContext;
    }
}
