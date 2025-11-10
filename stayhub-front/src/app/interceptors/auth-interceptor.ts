import { HttpInterceptorFn } from '@angular/common/http';
import {environmentDev} from '../../environments/environment.dev';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('authToken');

  if (noAuthEndpoints.some(route => req.url.includes(route))){
    return next(req);
  }

  if (token && req.url.startsWith(environmentDev.apiUrl)){
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  }
  return next(req);
};

const noAuthEndpoints = [
  "/users/login",
  "/users/register",
  "/users/request-password-reset",
]
