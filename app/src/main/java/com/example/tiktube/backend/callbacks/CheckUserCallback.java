package com.example.tiktube.backend.callbacks;

import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.utils.Enums;

public interface CheckUserCallback {
    void onSuccess(Enums.UserType user);
    void onFailure(Exception e);
}
