import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Retrieve the secure token we get from Spring Boot
  const token = localStorage.getItem('ENTERPRISE_JWT');

  // 2. If the token exists, clone the request and stamp the secure header on it
  if (token) {
    const secureRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Send the secured request to Spring Boot
    return next(secureRequest);
  }

  // 3. If no token (like when first logging in), send the normal request
  return next(req);
};