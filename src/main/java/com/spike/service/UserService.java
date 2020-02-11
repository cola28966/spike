package com.spike.service;

import com.spike.dao.UserDao;
import com.spike.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;
    public User getById(int id){
        return userDao.getById(id);
    }
}
