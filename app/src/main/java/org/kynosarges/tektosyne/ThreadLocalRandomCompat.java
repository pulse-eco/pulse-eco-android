package org.kynosarges.tektosyne;

import java.security.SecureRandom;
import java.util.Random;

public class ThreadLocalRandomCompat {

  private static ThreadLocal<Random> RANDOM_INSTANCE = new ThreadLocal<Random>() {
    @Override
    public Random get() {
      return new SecureRandom();
    }
  };

  public static Random current() {
    return RANDOM_INSTANCE.get();
  }

}
