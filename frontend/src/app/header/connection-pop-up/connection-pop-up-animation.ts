import { trigger, transition, style, animate } from '@angular/animations';

export const modeSwitchAnimation = trigger('modeSwitch', [
  transition(':enter', [
    style({ opacity: 0, transform: 'translateX(20%) scale(0.95)' }),
    animate('400ms ease-out', style({ opacity: 1, transform: 'translateX(0) scale(1)' }))
  ]),
  transition(':leave', [
    animate('400ms ease-in', style({ opacity: 0, transform: 'translateX(-20%) scale(0.95)' }))
  ])
]);