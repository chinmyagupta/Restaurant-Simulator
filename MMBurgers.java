import java.util.*;


public class MMBurgers implements MMBurgersInterface {
    int current_time = 0;
    Integer total_wait_time = 0;
    int N = 0;
    Deque<BurgerOrder> cooking = new LinkedList<>();
    Deque<BurgerOrder> near_chef = new LinkedList<>();
    int cooking_count;
    int M;
    int K;
    int alpha;

    int[] queues_size;
    ArrayList<LinkedList<Customer>> billing_queues;
    int queue_count = 0;
    Heap heap = new Heap();
    Vector<Customer> customers = new Vector<>();
    AVLTree avl = new AVLTree();

    public boolean isEmpty() {
        return queue_count == 0 && near_chef.size() == 0 && cooking.size() == 0;
    }

    public void setK(int k) throws IllegalNumberException {
        if (k <= 0) throw new IllegalNumberException("K must be greater than 0.");
        this.K = k;
        this.queues_size = new int[k];
        this.billing_queues = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            billing_queues.add(new LinkedList<>());
            queues_size[i] = 0;
        }
    }

    public void setM(int m) throws IllegalNumberException {
        if (m <= 0) throw new IllegalNumberException("M must be greater than 0.");
        cooking_count = m;
        M = m;
    }

    public void advanceTime(int t) throws IllegalNumberException {
        if (t < 0) throw new IllegalNumberException("Time cannot be negative.");
        if (current_time >= t) return;

        while (current_time < t) {
            int a, b;
            if (queue_count > 0) a = customers.get(heap.peek().idx).chef_time;
            else a = t;
            if (cooking_count == M) b = a;
            else b = cooking.peek().out_time;
            if (a <= t && a <= b) current_time = a;
            else if (b <= a && b <= t) current_time = b;
            else current_time = t;
            queue_to_chef();
            complete_cooking();
            renew_griddle();
            if (queue_count == 0 && cooking_count == M && near_chef.size() == 0) current_time = t;
        }
    }

    int queue_to_enter() {
        int mn_idx = -1;
        int mn_size = queues_size[0];
        for (int i = 0; i < K; i++) {
            if (queues_size[i] < mn_size) {
                mn_size = queues_size[i];
                mn_idx = i;
            }
        }
        if (mn_idx == -1) return 0;
        return mn_idx;
    }

    public void arriveCustomer(int id, int t, int numb) throws IllegalNumberException {
        if (t < current_time) throw new IllegalNumberException("Time should be greater than previous commands.");
        if (id <= N) throw new IllegalNumberException("ID already exists.");
        if (id > N + 1) throw new IllegalNumberException("Wrong ID sent.");
        N++;
        advanceTime(t);
        int q_idx = queue_to_enter();

        Customer cust = new Customer(id, numb, q_idx, t);
        cust.position_number = alpha;
        customers.add(cust);

        queues_size[q_idx]++;

        if (queues_size[q_idx] != 1) {
            Customer oldC = billing_queues.get(q_idx).getLast();
            cust.chef_time = Math.max(oldC.chef_time + q_idx + 1, t + q_idx + 1);
        } else cust.chef_time = q_idx + 1 + t;
        heap.add(cust.chef_time, alpha);
        billing_queues.get(q_idx).add(cust);

        avl.insert(id);
        AVL_node c = avl.get_customer(id);
        c.idx = alpha;
        queue_count++;
        alpha++;
    }

    public int customerState(int id, int t) throws IllegalNumberException {
        if (id < 0) throw new IllegalNumberException("Id must be natural number.");
        if (t < current_time) throw new IllegalNumberException("Time should be greater than previous commands.");
        boolean exists = avl.search_customer(id);
        if (!exists) return 0;
        advanceTime(t);
        Customer customer = customers.get(avl.get_customer(id).idx);
        if (customer.in_time > t) return 0;
        if (customer.before_chef) return customer.q_index + 1;
        if (customer.out_of_restaurant) return K + 2;
        return K + 1;
    }

    public int griddleState(int t) throws IllegalNumberException {
        if (t < current_time) throw new IllegalNumberException("Time should be greater than previous commands.");
        advanceTime(t);
        int cnt = 0;
        for (BurgerOrder o : cooking) cnt += o.count;
        return cnt;

    }

    public int griddleWait(int t) throws IllegalNumberException {
        if (t < current_time) throw new IllegalNumberException("Time should be greater than previous commands.");
        advanceTime(t);
        int cnt = 0;
        for (BurgerOrder o : near_chef) cnt += o.count;
        return cnt;
    }

    public int customerWaitTime(int id) throws IllegalNumberException {
        if (id > N) throw new IllegalNumberException("Id doesn't exist");
        return customers.get(avl.get_customer(id).idx).inside_time;
    }

    public float avgWaitTime() {
        float avg_wait = ((float) (total_wait_time)) / N;
        return avg_wait;
    }

    void queue_to_chef() {
        ArrayList<BurgerOrder> to_transfer = new ArrayList<>();
        while (queue_count > 0) {
            Customer customer = customers.get(heap.peek().idx);
            if (current_time == customer.chef_time) {
                BurgerOrder order = new BurgerOrder(customer.c_id, customer.q_index, customer.burger_required, customer.position_number);
                order.chef_time = current_time;
                to_transfer.add(order);
                queue_count--;
                heap.poll();
                queues_size[customer.q_index]--;
                customer.before_chef = false;
                billing_queues.get(customer.q_index).removeFirst();
            } else {
                break;
            }
        }
        to_transfer.sort((o1, o2) -> o2.q_index - o1.q_index);
        near_chef.addAll(to_transfer);
    }

    void renew_griddle() {
        while (near_chef.size() > 0) {
            BurgerOrder order = near_chef.removeFirst();
            if (order.count > cooking_count) {
                BurgerOrder ordA = new BurgerOrder(order.c_id, order.q_index, cooking_count, order.c_position);
                ordA.out_time = current_time + 10;
                cooking.add(ordA);
                BurgerOrder ordB = new BurgerOrder(order.c_id, order.q_index, order.count - cooking_count, order.c_position);
                near_chef.addFirst(ordB);
                cooking_count = 0;
            } else {
                cooking_count -= order.count;
                order.out_time = current_time + 10;
                cooking.add(order);
            }
            if (cooking_count <= 0) return;
        }
    }


    void complete_cooking() {
        int cnt = 0;
        while (cnt < cooking.size()) {
            if (cooking.peek().out_time != current_time) return;
            BurgerOrder order = cooking.peek();
            cooking_count += order.count;
            cooking.removeFirst();
            Customer completed = customers.get(order.c_position);
            if (order.count != completed.burger_required) {
                completed.burger_required -= order.count;
                continue;
            }
            completed.inside_time = current_time - completed.in_time + 1;
            completed.out_of_restaurant = true;
            total_wait_time += completed.inside_time;
        }
    }


}



