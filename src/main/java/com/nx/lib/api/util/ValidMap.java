package com.nx.lib.api.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nx.lib.exception.BadRequestException;

/**
 * 맵으로 된 body Parameter를 검증할때 사용
 * 
 * example
 * 
 * new Validator(params) .require("name") .alert();
 */
public class ValidMap {
    private Map<String, Object> params;
    private Set<String> errorMsg;
    private Boolean isValid;

    public ValidMap(Validator validator) {
        this.params = validator.params;
        this.errorMsg = validator.errorMsg;
        this.isValid = validator.isValid;
    }

    public Set<String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Set<String> errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public static class Validator {
        protected Logger logger = LoggerFactory.getLogger(this.getClass());
        private final Map<String, Object> params;
        private final Set<String> errorMsg;
        private Boolean isValid;

        public Validator(Map<String, Object> params) {
            if (params == null)
                throw new BadRequestException("4000", "parameter map is require");

            this.errorMsg = new HashSet<>();
            this.params = params;
        }

        public Validator predicate(String k, Predicate<Object> pre) {
            checkMap();

            if (isEmpty(k)) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object v = params.get(k);
            valid(pre.test(v), k + " (valid fail)");
            return this;
        }

        public <T> Validator predicate(String k, Predicate<T> pre, Class<T> c) {
            checkMap();

            if (isEmpty(k)) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object v = params.get(k);
            T t;
            try {
                t = c.cast(v);
            } catch (ClassCastException e) {
                throw new BadRequestException("4000", "ClassCastException : " + e.getMessage());
            }

            valid(pre.test(t), k + " (valid fail)");
            return this;
        }

        public Validator require(String k) {
            checkMap();

            valid(!isEmpty(k), k + " (require)");
            return this;
        }

        public Validator contains(String k, Object... s) {
            checkMap();

            if (isEmpty(k) || s == null) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object o = params.get(k);
            boolean bool = false;
            for (Object vo : s) {
                if (o.equals(vo)) {
                    bool = true;
                    break;
                }
            }
            valid(bool, k + " (not contains)");

            return this;
        }

        public Validator equals(String k, Object v) {
            checkMap();

            if (isEmpty(k) || v == null) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            valid(params.get(k).equals(v), k);
            return this;
        }

        public Validator between(String k, int v1, int v2) {
            checkMap();

            if (isEmpty(k)) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object v = params.get(k);
            if (v != null && v instanceof Integer) {
                int parsValue = Integer.valueOf(v.toString());
                valid(v1 <= parsValue && v2 >= parsValue, k + " (Range)");
            }
            return this;
        }

        public Validator upper(String k, int v1) {
            checkMap();

            if (isEmpty(k)) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object v = params.get(k);
            if (v != null && v instanceof Integer) {
                int parsValue = Integer.valueOf(v.toString());
                valid(v1 <= parsValue, k + " (" + v1 + "자 이상)");
            } else if (v != null && v instanceof String) {
                String parsValue = String.valueOf(v);
                valid(v1 <= parsValue.length(), k + " (" + v1 + "자 이상)");
            }
            return this;
        }

        public Validator under(String k, int v1) {
            checkMap();

            if (isEmpty(k)) {
                addMsg(k + " (empty:uncheck)");
                return this;
            }

            Object v = params.get(k);
            if (v != null && v instanceof Integer) {
                int parsValue = Integer.valueOf(v.toString());
                valid(v1 >= parsValue, k + " (" + v1 + "자 이하)");
            } else if (v != null && v instanceof String) {
                String parsValue = String.valueOf(v);
                valid(v1 >= parsValue.length(), k + " (" + v1 + "자 이하)");
            }
            return this;
        }

        private boolean isEmpty(String k) {
            return (!params.containsKey(k) || params.get(k) == null
                    || (params.get(k) != null && "".equals(params.get(k).toString())));
        }

        private void checkMap() {
            if (params == null)
                throw new BadRequestException("4000", "parameter map is require");
        }

        private void valid(boolean bool, String msg) {
            if (!bool)
                addMsg(msg);

            if (isValid == null) {
                isValid = bool;
            } else {
                isValid = isValid && bool;
            }

        }

        private void addMsg(String msg) {
            errorMsg.add(msg);
        }

        public void alert() {
            if (!isValid)
                throw new BadRequestException("4000", "Errors : " + errorMsg.toString());
        }

        public Validator logging() {
            if (errorMsg.size() > 0)
                System.out.println(errorMsg.toString());
            logger.debug("Validation Message \n", errorMsg.toString());

            return this;
        }

        public ValidMap result() {
            return new ValidMap(this);
        }
    }
}
