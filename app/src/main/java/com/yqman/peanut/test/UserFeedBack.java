package com.yqman.peanut.test;

/**
 * Created by manyongqiang on 2018/5/9.
 */
public class UserFeedBack {
    private static final String TAG = "UserFeedBack";
    private static final int DEFAULT_PERSON_COUNT = 10;
    private static final int DEFAULT_PERSON_INDEX = -10;
    private int mSum;
    private int mPersonCount = DEFAULT_PERSON_COUNT;
    private int mSelfIndex = DEFAULT_PERSON_INDEX;

    public UserFeedBack(int sum) {
        mSum = sum;
    }

    public void setPersonCount(int personCount) {
        mPersonCount = personCount;
    }

    public void setSelfIndex(int selfIndex) {
        mSelfIndex = selfIndex;
    }

    public String dump() {
        int everyoneCount = mSum / mPersonCount;
        int startPosition = 0;
        int endPosition = 0;

        StringBuilder builder = new StringBuilder();
        for (int personIndex = 0; personIndex < mPersonCount; personIndex++) {
            if (personIndex == mPersonCount -1) {
                endPosition = mSum;
            } else {
                endPosition = startPosition + everyoneCount;
            }
            if ((personIndex + 1) == mSelfIndex) {
                builder.append("myself is follow position！！！！");
                builder.append("\r\n");
            } else {
                builder.append("person:" );
                builder.append(personIndex + 1);
                builder.append("\r\n");
            }
            builder.append(startPosition);
            builder.append("-");
            builder.append(endPosition);
            builder.append("\r\n");
            startPosition = endPosition;
        }
        return builder.toString();
    }
}
